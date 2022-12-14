package com.example.ll.finalproject.order.service;

import com.example.ll.finalproject.cart.entity.CartItem;
import com.example.ll.finalproject.cart.service.CartService;
import com.example.ll.finalproject.member.entity.Member;
import com.example.ll.finalproject.member.servie.MemberService;
import com.example.ll.finalproject.order.entity.Order;
import com.example.ll.finalproject.order.entity.OrderItem;
import com.example.ll.finalproject.order.repository.OrderRepository;
import com.example.ll.finalproject.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@PropertySource("classpath:application-apikey.yml")
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final CartService cartService;
    private final OrderRepository orderRepository;
    private final MemberService memberService;

    @Value("${payment_serverKey}")
    private String SECRET_KEY;

    public String getSECRET_KEY(){
        return SECRET_KEY;
    }
    @Transactional
    public Order createFromCart(Member buyer) {

        // 입력된 회원의 장바구니 아이템들을 전부 가져온다.

        // 만약에 특정 장바구니의 상품옵션이 판매불능이면 삭제
        // 만약에 특정 장바구니의 상품옵션이 판매가능이면 주문품목으로 옮긴 후 삭제

        List<CartItem> cartItems = cartService.getItemsByBuyer(buyer);

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            if (product.isOrderable()) {
                orderItems.add(new OrderItem(product));
            }

            cartService.removeItem(cartItem);
        }

        return create(buyer, orderItems);
    }
    //주문품목 리스트와 구매자를 받아 주문을 만듬
    @Transactional
    public Order create(Member buyer, List<OrderItem> orderItems) {
        Order order = Order
                .builder()
                .buyer(buyer)
                .build();

        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }

        // 주문 품목으로 부터 이름을 만든다.
        order.makeName();

        orderRepository.save(order);

        return order;
    }

    //비용 지불
    @Transactional
    public void payByRestCashOnly(Order order) {
        Member buyer = order.getBuyer();

        long restCash = buyer.getRestCash();

        int payPrice = order.calculatePayPrice();

        if (payPrice > restCash) {
            throw new RuntimeException("예치금이 부족합니다.");
        }

        memberService.addCash(buyer, payPrice * -1, "주문__%d__사용__예치금".formatted(order.getId()));

        order.setPaymentDone();
        orderRepository.save(order);
    }

    @Transactional
    public void refund(Order order) {
        int payPrice = order.getPayPrice();
        memberService.addCash(order.getBuyer(), payPrice, "주문__%d__환불__예치금".formatted(order.getId()));

        order.setRefundDone();
        orderRepository.delete(order);
    }

    public Optional<Order> findForPrintById(long id) {
        return findById(id);
    }
    private Optional<Order> findById(long id) {
        return orderRepository.findById(id);
    }

    public boolean actorCanSee(Member actor, Order order) {
        return actor.getId().equals(order.getBuyer().getId());
    }

    public List<Order> getOrderByBuyer(Member buyer) {
        List<Order> orders = orderRepository.findAllByBuyerId(buyer.getId());
        if(orders==null){
            return null;
        }
        return orders;
    }

    public void cancelOrder(Order order) {
        orderRepository.delete(order);
    }

    public boolean actorCanPayment(Member actor, Order order) {
        return actor.getId().equals(order.getBuyer().getId());
    }

    
    //10분 이내만 환불 가능
    public boolean isTenMinute(LocalDateTime modifyDate) {
        LocalDateTime nowTime = LocalDateTime.now();
        Duration duration = Duration.between(modifyDate, nowTime);
        long dif = duration.getSeconds();
//        System.out.println(dif+"초");
        if(dif <= 600){
            return true;
        }
        return false;
    }

    @Transactional
    public void payByTossPayments(Order order, long useRestCash) {
        Member buyer = order.getBuyer();
        int payPrice = order.calculatePayPrice();

        long pgPayPrice = payPrice - useRestCash;
        memberService.addCash(buyer, pgPayPrice, "주문__%d__충전__토스페이먼츠".formatted(order.getId()));
        memberService.addCash(buyer, pgPayPrice * -1, "주문__%d__사용__토스페이먼츠".formatted(order.getId()));

        if ( useRestCash > 0 ) {
            memberService.addCash(buyer, useRestCash * -1, "주문__%d__사용__예치금".formatted(order.getId()));
        }

        order.setPaymentDone();
        orderRepository.save(order);
    }

}
