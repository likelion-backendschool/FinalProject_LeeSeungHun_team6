package com.example.ll.finalproject.app.myBook.service;

import com.example.ll.finalproject.app.member.entity.Member;
import com.example.ll.finalproject.app.myBook.entity.MyBook;
import com.example.ll.finalproject.app.myBook.repository.MyBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyBookService {
    private final MyBookRepository myBookRepository;
    public MyBook write(Member author, String subject, String content) {
        MyBook mybook = MyBook.builder()
                .author(author)
                .subject(subject)
                .content(content)
                .build();

        myBookRepository.save(mybook);

        return mybook;
    }
}
