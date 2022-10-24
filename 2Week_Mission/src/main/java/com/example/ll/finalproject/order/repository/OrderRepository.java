package com.example.ll.finalproject.order.repository;

import com.example.ll.finalproject.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
