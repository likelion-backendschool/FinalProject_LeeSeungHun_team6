package com.example.ll.finalproject.app.order.repository;

import com.example.ll.finalproject.app.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByBuyerId(Long id);


    List<Order> findAllByBuyerIdAndIsRefunded(Long id, boolean b);
}
