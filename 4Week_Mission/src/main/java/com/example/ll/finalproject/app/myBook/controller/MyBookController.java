package com.example.ll.finalproject.app.myBook.controller;

import com.example.ll.finalproject.app.base.dto.RsData;
import com.example.ll.finalproject.app.myBook.entity.MyBook;
import com.example.ll.finalproject.app.myBook.service.MyBookService;
import com.example.ll.finalproject.app.security.dto.MemberContext;
import com.example.ll.finalproject.util.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/api/v1/myBooks")
public class MyBookController {
    private final MyBookService myBookService;
    @GetMapping("")
    public ResponseEntity<RsData> list(@AuthenticationPrincipal MemberContext memberContext) {
        List<MyBook> myBooks = myBookService.findAllByAuthorId(memberContext.getMember().getId());

        return Util.spring.responseEntityOf(
                RsData.successOf(
                        Util.mapOf(
                                "myBooks", myBooks
                        )
                )
        );
    }
    @GetMapping("{id}")
    public ResponseEntity<RsData> detail(@PathVariable Long id,@AuthenticationPrincipal MemberContext memberContext) {
        MyBook mybook = myBookService.findById(id).orElse(null);

        if (myBookService.actorCanRead(memberContext, mybook) == false) {
            return Util.spring.responseEntityOf(
                    RsData.of(
                            "F-2",
                            "읽을 권한이 없습니다."
                    )
            );
        }

        if (mybook == null) {
            return Util.spring.responseEntityOf(
                    RsData.of(
                            "F-1",
                            "해당 게시물은 존재하지 않습니다."
                    )
            );
        }

        return Util.spring.responseEntityOf(
                RsData.successOf(
                        Util.mapOf(
                                "mybook", mybook
                        )
                )
        );
    }
}
