package com.example.ll.finalproject.withdraw.controller;

import com.example.ll.finalproject.member.entity.Member;
import com.example.ll.finalproject.member.servie.MemberService;
import com.example.ll.finalproject.security.dto.MemberContext;
import com.example.ll.finalproject.util.Ut;
import com.example.ll.finalproject.withdraw.dto.request.WithdrawForm;
import com.example.ll.finalproject.withdraw.service.WithdrawService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class WithdrawController {
    private final WithdrawService withdrawService;
    @GetMapping("/withdraw/apply")
    @PreAuthorize("isAuthenticated()")
    public String showWithdraw(@AuthenticationPrincipal MemberContext memberContext, Model model){

        model.addAttribute("memberCash", memberContext.getRestCash());
        return "withdraw/apply";
    }

    @PostMapping("/withdraw/apply")
    @PreAuthorize("isAuthenticated()")
    public String withdrawApply(@AuthenticationPrincipal MemberContext memberContext, WithdrawForm withdrawForm){
        if(memberContext.getRestCash() < withdrawForm.getPrice()){
            return "redirect:/member/modify?errorMsg=%s".formatted(Ut.url.encode("현재 금액을 확인해주세요."));
        }
        Member member = memberContext.getMember();
        withdrawService.create(member, withdrawForm);

//        Member member = memberContext.getMember();
//        memberService.addCash(member, withdrawForm.getPrice() * -1, "출금__일반");
//        memberContext.setRestCash(member.getRestCash());
//        Authentication authentication = new UsernamePasswordAuthenticationToken(memberContext, member.getPassword(), memberContext.getAuthorities());
//        SecurityContextHolder.getContext().setAuthentication(authentication);

        return "redirect:/member/modify?msg=%s".formatted(Ut.url.encode("출금 신청이 완료되었습니다."));
    }
}
