package com.example.ll.finalproject.mybook.repository;

import com.example.ll.finalproject.mybook.entity.MyBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MyBookRepository extends JpaRepository<MyBook, Long> {

    List<MyBook> findAllByMemberId(Long id);

    MyBook findByMemberIdAndProductId(Long id, Long id1);
}
