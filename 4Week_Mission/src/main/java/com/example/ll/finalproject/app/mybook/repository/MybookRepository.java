package com.example.ll.finalproject.app.mybook.repository;

import com.example.ll.finalproject.app.mybook.entity.Mybook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MybookRepository extends JpaRepository<Mybook, Long> {
    List<Mybook> findAllByOrderByIdDesc();
}
