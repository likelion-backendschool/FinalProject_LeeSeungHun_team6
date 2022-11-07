package com.example.ll.finalproject.app.withdraw.controller;

import com.example.ll.finalproject.app.member.entity.Member;
import com.example.ll.finalproject.app.member.service.MemberService;
import com.example.ll.finalproject.app.security.dto.MemberContext;
import com.example.ll.finalproject.app.util.Ut;
import com.example.ll.finalproject.app.withdraw.dto.request.WithdrawForm;
import com.example.ll.finalproject.app.withdraw.entity.Withdraw;
import com.example.ll.finalproject.app.withdraw.service.WithdrawService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class WithdrawController {
    private final WithdrawService withdrawService;
    private final MemberService memberService;
    @GetMapping("/withdraw/apply")
    @PreAuthorize("isAuthenticated()")
    public String showWithdraw(@AuthenticationPrincipal MemberContext memberContext, Model model){

        model.addAttribute("memberCash", memberContext.getRestCash());
        return "withdraw/apply";
    }

    @PostMapping("/withdraw/apply")
    @PreAuthorize("isAuthenticated()")
    public String requestApply(@AuthenticationPrincipal MemberContext memberContext, WithdrawForm withdrawForm){
        if(memberContext.getRestCash() < withdrawForm.getPrice()){
            return "redirect:/member/modify?errorMsg=%s".formatted(Ut.url.encode("현재 금액을 확인해주세요."));
        }
        Member member = memberContext.getMember();
        withdrawService.create(member, withdrawForm);

        return "redirect:/member/modify?msg=%s".formatted(Ut.url.encode("출금 신청이 완료되었습니다."));
    }

    @GetMapping("/adm/withdraw/applyList")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String showWithdrawList(Model model){

        List<Withdraw> withdraws = withdrawService.findAllApplyList();

        model.addAttribute("withdraws", withdraws);
        return "withdraw/applyList";
    }
    @PostMapping("/adm/withdraw/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String withdrawApply(Model model, @PathVariable long id){

        Withdraw withdraw = withdrawService.findById(id);
        if(withdraw.getMember().getRestCash() < withdraw.getPrice()){
            return "redirect:/adm/withdraw/applyList?errorMsg=%s".formatted(Ut.url.encode("사용자가 돈을 사용하였습니다."));
        }
        withdrawService.apply(withdraw);

        List<Withdraw> withdraws = withdrawService.findAllApplyList();

        model.addAttribute("withdraws", withdraws);

        return "redirect:/adm/withdraw/applyList?msg=%s".formatted(Ut.url.encode("출금이 완료되었습니다."));
    }

    @PostMapping("/adm/withdraw/withdrawCheck")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String withdrawApplyList(Model model, String ids){
        String[] idsArr = ids.split(",");

        for(String s : idsArr){
            long id = Long.parseLong(s);
            Withdraw withdraw = withdrawService.findById(id);
            if(withdraw.getMember().getRestCash() < withdraw.getPrice()){
                return "redirect:/adm/withdraw/applyList?errorMsg=%s".formatted(Ut.url.encode(id+"번 출금 실패, 사용자가 돈을 사용"));
            }
            withdrawService.apply(withdraw);
        }
        List<Withdraw> withdraws = withdrawService.findAllApplyList();

        model.addAttribute("withdraws", withdraws);
        return "redirect:/adm/withdraw/applyList?msg=%s".formatted(Ut.url.encode("출금이 완료되었습니다."));
    }

}
