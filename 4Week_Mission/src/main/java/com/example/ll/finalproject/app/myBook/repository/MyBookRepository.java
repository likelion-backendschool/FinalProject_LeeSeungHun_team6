package com.example.ll.finalproject.app.myBook.repository;

import com.example.ll.finalproject.app.member.entity.Member;
import com.example.ll.finalproject.app.myBook.entity.MyBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MyBookRepository extends JpaRepository<MyBook, Long> {
    List<MyBook> findAllByOrderByIdDesc();

    List<MyBook> findAllByAuthorId(Long id);
}
