package com.example.ll.finalproject.order.controller;

import com.example.ll.finalproject.cart.entity.CartItem;
import com.example.ll.finalproject.cart.service.CartService;
import com.example.ll.finalproject.member.entity.Member;
import com.example.ll.finalproject.member.servie.MemberService;
import com.example.ll.finalproject.mybook.service.MyBookService;
import com.example.ll.finalproject.order.entity.Order;
import com.example.ll.finalproject.order.entity.OrderItem;
import com.example.ll.finalproject.order.exception.ActorCanNotPayOrderException;
import com.example.ll.finalproject.order.exception.ActorCanNotSeeOrderException;
import com.example.ll.finalproject.order.exception.OrderIdNotMatchedException;
import com.example.ll.finalproject.order.service.OrderService;
import com.example.ll.finalproject.product.entity.Product;
import com.example.ll.finalproject.security.dto.MemberContext;
import com.example.ll.finalproject.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    private final MemberService memberService;
    private final CartService cartService;
    private final MyBookService mybookService;

    @GetMapping("/list")
    @PreAuthorize("isAuthenticated()")
    public String showOrder(@AuthenticationPrincipal MemberContext memberContext, Model model) {
        Member buyer = memberContext.getMember();
        List<Order> orders = orderService.getOrderByBuyer(buyer);

        model.addAttribute("orders", orders);

        return "order/list";
    }
    
    
    //선택된 상품만 주문하기
    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public String makeOrder(@AuthenticationPrincipal MemberContext memberContext, String ids) {

        Member buyer = memberContext.getMember();
        List<OrderItem> orderItemList = new ArrayList<>();

        String[] idsArr = ids.split(",");
        Arrays.stream(idsArr)
                .mapToLong(Long::parseLong)
                .forEach(id -> {
                    CartItem cartItem = cartService.findItemById(id).orElse(null);

                    if (cartService.actorCanDelete(buyer, cartItem)) {
                        Product product = cartItem.getProduct();
                        orderItemList.add(new OrderItem(product));
                        cartService.removeItem(cartItem);
                    }
                });

        Order order = orderService.create(buyer, orderItemList);
        String redirect = "redirect:/order/%d".formatted(order.getId()) + "?msg=" + Ut.url.encode("%d번 주문이 생성되었습니다.".formatted(order.getId()));

        return redirect;
    }
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public String showDetail(@AuthenticationPrincipal MemberContext memberContext, @PathVariable long id, Model model) {
        Order order = orderService.findForPrintById(id).get();

        Member actor = memberContext.getMember();

        long restCash = memberService.getRestCash(actor);

        if (orderService.actorCanSee(actor, order) == false) {
            throw new ActorCanNotSeeOrderException();
        }

        model.addAttribute("order", order);
        model.addAttribute("actorRestCash", restCash);

        return "order/detail";
    }

    @GetMapping("/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    public String cancelOrder(@AuthenticationPrincipal MemberContext memberContext, @PathVariable long id, Model model) {
        Order order = orderService.findForPrintById(id).get();

        Member actor = memberContext.getMember();

        if (orderService.actorCanSee(actor, order) == false) {
            throw new ActorCanNotSeeOrderException();
        }
        orderService.cancelOrder(order);
        String msg = "%d번 주문이 삭제되었습니다.".formatted(order.getId());
        msg = Ut.url.encode(msg);
        return "redirect:/product/list?msg=%s".formatted(msg);
    }
    @GetMapping("/{id}/refund")
    @PreAuthorize("isAuthenticated()")
    public String refundOrder(@AuthenticationPrincipal MemberContext memberContext, @PathVariable long id, Model model) {
        Order order = orderService.findForPrintById(id).get();

        Member actor = memberContext.getMember();

        if (orderService.actorCanSee(actor, order) == false) {
            throw new OrderIdNotMatchedException();
        }
        String msg;
        if(orderService.isTenMinute(order.getModifyDate())){
            orderService.refund(order);
            msg = "%d번 주문이 환불되었습니다.".formatted(order.getId());
        }
        else{
            msg = "%d번 주문 환불 실패 *10분 이내만 환불 가능.".formatted(order.getId());
        }

        msg = Ut.url.encode(msg);
        return "redirect:/order/list?msg=%s".formatted(msg);
    }
    //결제 처리
    @PostMapping("/{id}/pay")
    @PreAuthorize("isAuthenticated()")
    public String payByRestCashOnly(@AuthenticationPrincipal MemberContext memberContext, @PathVariable long id) {
        Order order = orderService.findForPrintById(id).get();

        Member actor = memberContext.getMember();

        long restCash = memberService.getRestCash(actor);

        if (orderService.actorCanPayment(actor, order) == false) {
            throw new ActorCanNotPayOrderException();
        }

        orderService.payByRestCashOnly(order);
        List<OrderItem> orderItems = order.getOrderItems();

        List<Product> products = new ArrayList<>();
        for(OrderItem orderItem:orderItems){
            products.add(orderItem.getProduct());
        }
        mybookService.addProduct(actor, products);

        return "redirect:/order/%d?msg=%s".formatted(order.getId(), Ut.url.encode("예치금으로 결제했습니다."));
    }

}
