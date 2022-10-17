package com.example.ll.finalproject.member.controller;

import com.example.ll.finalproject.member.servie.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("member")
public class MemberController {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
}
