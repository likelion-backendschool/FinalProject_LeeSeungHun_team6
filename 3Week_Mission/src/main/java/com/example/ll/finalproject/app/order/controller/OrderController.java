package com.example.ll.finalproject.app.order.controller;

import com.example.ll.finalproject.app.cart.entity.CartItem;
import com.example.ll.finalproject.app.cart.service.CartService;
import com.example.ll.finalproject.app.member.entity.Member;
import com.example.ll.finalproject.app.member.service.MemberService;
import com.example.ll.finalproject.app.mybook.service.MyBookService;
import com.example.ll.finalproject.app.order.entity.Order;
import com.example.ll.finalproject.app.order.entity.OrderItem;
import com.example.ll.finalproject.app.order.exception.ActorCanNotPayOrderException;
import com.example.ll.finalproject.app.order.exception.ActorCanNotSeeOrderException;
import com.example.ll.finalproject.app.order.exception.OrderIdNotMatchedException;
import com.example.ll.finalproject.app.order.exception.OrderNotEnoughRestCashException;
import com.example.ll.finalproject.app.order.service.OrderService;
import com.example.ll.finalproject.app.product.entity.Product;
import com.example.ll.finalproject.app.security.dto.MemberContext;
import com.example.ll.finalproject.app.util.Ut;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    private final MemberService memberService;
    private final CartService cartService;
    private final MyBookService mybookService;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;

    @GetMapping("/list")
    @PreAuthorize("isAuthenticated()")
    public String showOrder(@AuthenticationPrincipal MemberContext memberContext, Model model) {
        Member buyer = memberContext.getMember();
        List<Order> orders = orderService.getOrderByBuyer(buyer);

        model.addAttribute("orders", orders);

        return "order/list";
    }
    

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
                    /* ????????? ????????? ????????????
                    * ????????? ????????? orderItemList??? ???????????? ???????????? ????????????.
                    */
                    if (cartService.actorCanDelete(buyer, cartItem)) {
                        Product product = cartItem.getProduct();
                        orderItemList.add(new OrderItem(product));
                        cartService.removeItem(cartItem);
                    }
                });

        Order order = orderService.create(buyer, orderItemList); //?????? ??????, ?????? ??? ??????
        String redirect = "redirect:/order/%d".formatted(order.getId()) + "?msg=" + Ut.url.encode("%d??? ????????? ?????????????????????.".formatted(order.getId()));

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
    public String cancelOrder(@AuthenticationPrincipal MemberContext memberContext, @PathVariable long id) {
        Order order = orderService.findForPrintById(id).get();

        Member actor = memberContext.getMember();

        if (orderService.actorCanSee(actor, order) == false) {
            throw new ActorCanNotSeeOrderException();
        }
        /* ?????? ????????? ?????? ?????? */
        orderService.cancelOrder(order);
        String msg = "%d??? ????????? ?????????????????????.".formatted(order.getId());
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
        /*
        * 10??? ????????? ???????????? ?????? ??????
        * */
        String msg;
        if(orderService.isTenMinute(order.getModifyDate())){
            orderService.refund(order);
            msg = "%d??? ????????? ?????????????????????.".formatted(order.getId());
        }
        else{
            msg = "%d??? ?????? ?????? ?????? *10??? ????????? ?????? ??????.".formatted(order.getId());
        }

        msg = Ut.url.encode(msg);
        return "redirect:/order/list?msg=%s".formatted(msg);
    }
    //?????? ??????
    @PostMapping("/{id}/pay")
    @PreAuthorize("isAuthenticated()")
    public String payByRestCashOnly(@AuthenticationPrincipal MemberContext memberContext, @PathVariable long id) {
        Order order = orderService.findForPrintById(id).get();

        Member actor = memberContext.getMember();

        long restCash = memberService.getRestCash(actor);

        if (orderService.actorCanPayment(actor, order) == false) {
            throw new ActorCanNotPayOrderException();
        }

        //?????????????????? ??????
        orderService.payByRestCashOnly(order);
        List<OrderItem> orderItems = order.getOrderItems();

        List<Product> products = new ArrayList<>();
        for(OrderItem orderItem:orderItems){
            products.add(orderItem.getProduct());
        }
        mybookService.addProduct(actor, products);

        return "redirect:/order/%d?msg=%s".formatted(order.getId(), Ut.url.encode("??????????????? ??????????????????."));
    }



    @PostConstruct
    private void init() {
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) {
                return false;
            }

            @Override
            public void handleError(ClientHttpResponse response) {
            }
        });
    }
    //?????? ??????
    private String SECRET_KEY = "";

    @RequestMapping("/{id}/success")
    public String confirmPayment(
            @PathVariable long id,
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam Long amount,
            Model model,
            @AuthenticationPrincipal MemberContext memberContext
    ) throws Exception {

        Order order = orderService.findForPrintById(id).get();

        long orderIdInputed = Long.parseLong(orderId.split("__")[1]);

        if (id != orderIdInputed) {
            throw new OrderIdNotMatchedException();
        }
        // ????????? ?????????
        SECRET_KEY = orderService.getSECRET_KEY();
        HttpHeaders headers = new HttpHeaders();
        // headers.setBasicAuth(SECRET_KEY, ""); // spring framework 5.2 ?????? ???????????? ??????
        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString((SECRET_KEY + ":").getBytes()));
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> payloadMap = new HashMap<>();
        payloadMap.put("orderId", orderId);
        payloadMap.put("amount", String.valueOf(amount));

        Member actor = memberContext.getMember();
        long restCash = memberService.getRestCash(actor);
        long payPriceRestCash = order.calculatePayPrice() - amount;

        if (payPriceRestCash > restCash) {
            throw new OrderNotEnoughRestCashException();
        }

        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(payloadMap), headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity(
                "https://api.tosspayments.com/v1/payments/" + paymentKey, request, JsonNode.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {

            orderService.payByTossPayments(order, payPriceRestCash);

            List<OrderItem> orderItems = order.getOrderItems();

            List<Product> products = new ArrayList<>();
            for(OrderItem orderItem:orderItems){
                products.add(orderItem.getProduct());
            }

            mybookService.addProduct(actor, products);

            return "redirect:/order/%d?msg=%s".formatted(order.getId(), Ut.url.encode("????????? ?????????????????????."));
        } else {
            JsonNode failNode = responseEntity.getBody();
            model.addAttribute("order",order);
            model.addAttribute("message", failNode.get("message").asText());
            model.addAttribute("code", failNode.get("code").asText());
            return "order/fail";
        }
    }
    @RequestMapping("/charge/{id}/success")
    public String confirmChargePayment(
            @PathVariable long id,
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam Long amount,
            Model model,
            @AuthenticationPrincipal MemberContext memberContext
    ) throws Exception {
        Member member = memberContext.getMember();
        // ????????? ?????????
        SECRET_KEY = orderService.getSECRET_KEY();
        HttpHeaders headers = new HttpHeaders();

        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString((SECRET_KEY + ":").getBytes()));
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> payloadMap = new HashMap<>();
        payloadMap.put("orderId", orderId);
        payloadMap.put("amount", String.valueOf(amount));


        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(payloadMap), headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity(
                "https://api.tosspayments.com/v1/payments/" + paymentKey, request, JsonNode.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            memberService.addCash(member, amount, "??????__??????????????????__??????");
            memberContext.setRestCash(member.getRestCash());
            Authentication authentication = new UsernamePasswordAuthenticationToken(memberContext, member.getPassword(), memberContext.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            model.addAttribute("member", member);
            return "redirect:/member/modify?msg=%s".formatted(Ut.url.encode("????????? ?????????????????????."));
        } else {
            JsonNode failNode = responseEntity.getBody();
            model.addAttribute("message", failNode.get("message").asText());
            model.addAttribute("code", failNode.get("code").asText());
            return "order/fail";
        }
    }

    @RequestMapping("/{id}/fail")
    public String failPayment(@RequestParam String message, @RequestParam String code, Model model,@PathVariable long id) {
        Order order = orderService.findForPrintById(id).orElse(null);
        model.addAttribute("order",order);
        model.addAttribute("message", message);
        model.addAttribute("code", code);
        return "order/fail";
    }
}
