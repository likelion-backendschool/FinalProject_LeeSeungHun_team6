package com.example.ll.finalproject.cash.repository;

import com.example.ll.finalproject.cash.entity.CashLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CashLogRepository extends JpaRepository<CashLog, Long> {
}
