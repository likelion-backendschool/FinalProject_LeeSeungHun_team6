package com.example.ll.finalproject.withdraw.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class WithdrawController {
    @GetMapping("/withdraw/apply")
    @PreAuthorize("isAuthenticated()")
    public String apply(){
        return "withdraw/apply";
    }
}
