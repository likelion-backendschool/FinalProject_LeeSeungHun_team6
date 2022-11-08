package com.example.ll.finalproject.app.myBook.service;

import com.example.ll.finalproject.app.member.entity.Member;
import com.example.ll.finalproject.app.myBook.entity.MyBook;
import com.example.ll.finalproject.app.myBook.repository.MyBookRepository;
import com.example.ll.finalproject.app.security.dto.MemberContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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


    public Optional<MyBook> findById(Long id) {
        return myBookRepository.findById(id);
    }

    public boolean actorCanRead(MemberContext memberContext, MyBook mybook) {
        return memberContext.getId() == mybook.getAuthor().getId();
    }

    public List<MyBook> findAllByAuthorId(Long id) {
        List<MyBook> myBooks = myBookRepository.findAllByAuthorId(id);
        if(myBooks==null){
            return null;
        }
        return myBooks;
    }
}
