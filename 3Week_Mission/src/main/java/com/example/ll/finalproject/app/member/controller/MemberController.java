package com.example.ll.finalproject.app.member.controller;

import com.example.ll.finalproject.app.member.dto.request.JoinForm;
import com.example.ll.finalproject.app.member.entity.Member;
import com.example.ll.finalproject.app.member.service.MemberService;
import com.example.ll.finalproject.app.security.dto.MemberContext;
import com.example.ll.finalproject.app.util.Ut;
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
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;

    @PreAuthorize("isAnonymous()")
    @GetMapping("/login")
    public String showLogin(HttpServletRequest request) {
        String uri = request.getHeader("Referer");
        if (uri != null && !uri.contains("/member") && !uri.contains("/order") && !uri.contains("/adm")) {
            request.getSession().setAttribute("prevPage", uri);
        }

        return "member/login";
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/registerAuthor")
    public String registerAuthorView() {
        return "member/registerAuthor";
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/registerAuthor")
    public String registerAuthor(@AuthenticationPrincipal MemberContext memberContext, String nickname) {
        if(nickname.trim().length()==0){
            return "redirect:/member/registerAuthor?errorMsg=" + Ut.url.encode("???????????? ??????????????????.");
        }
        Member member = memberService.registerAuthor(memberContext, nickname);
        // ????????? ????????? ????????? MemberContext ????????? ????????? ???????????? ?????? ??????
        Authentication authentication = new UsernamePasswordAuthenticationToken(memberContext, member.getPassword(), memberContext.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // ????????? ????????? ????????? MemberContext ????????? ????????? ???????????? ?????? ???
        return "redirect:/product/list?msg=" + Ut.url.encode("???????????? ?????????????????????.");
    }


    @PreAuthorize("isAnonymous()")
    @GetMapping("/join")
    public String showJoin() {
        return "member/join";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/join")
    public String join(@Valid JoinForm joinForm) {
//        memberService.join(joinForm.getUsername(), joinForm.getPassword(), joinForm.getEmail(), joinForm.getNickname());
        memberService.join(joinForm.getUsername(), joinForm.getPassword(), joinForm.getEmail());

        return "redirect:/member/login?msg=" + Ut.url.encode("??????????????? ?????????????????????.");
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify")
    public String modify(){
        return "member/modify";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify")
    public String modify(@AuthenticationPrincipal MemberContext memberContext, String email, String nickname) {
        Member member = memberService.getMemberById(memberContext.getId());

        memberService.modify(member, email, nickname);

        // ????????? ????????? ????????? MemberContext ????????? ????????? ???????????? ?????? ??????
        memberContext.setModifyDate(member.getModifyDate());
        memberContext.setEmail(member.getEmail());
        memberContext.setNickname(member.getNickname());
        Authentication authentication = new UsernamePasswordAuthenticationToken(memberContext, member.getPassword(), memberContext.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // ????????? ????????? ????????? MemberContext ????????? ????????? ???????????? ?????? ???

        return "redirect:/member/modify?msg=" + Ut.url.encode("????????? ?????????????????????.");
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modifyPassword")
    public String modifyPassword(){
        return "member/modifyPassword";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modifyPassword")
    public String modifyPassword(@AuthenticationPrincipal MemberContext context, String oldPassword, String password) {
        Member member = memberService.getMemberById(context.getId());
        if(!memberService.checkPassword(member, oldPassword)){
            return "redirect:/member/modifyPassword?errorMsg=" + Ut.url.encode("?????? ??????????????? ???????????? ????????????.");
        }
        memberService.modifyPassword(member, password);

        return "redirect:/member/modifyPassword?msg=" + Ut.url.encode("???????????? ????????? ?????????????????????.");
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/findUsername")
    public String findUsername(){
        return "member/findUsername";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/findUsername")
    public String findUsername(String email){
        String memberUsername = memberService.enrolledEmail(email);
        if(memberUsername==null){
            return "redirect:/member/findUsername?errorMsg=" + Ut.url.encode("???????????? ?????? ??????????????????.");
        }
        return "redirect:/member/findUsername?msg=" + Ut.url.encode("?????????: "+memberUsername);
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/findPassword")
    public String findPassword(){
        return "member/findPassword";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/findPassword")
    public String findPassword(String username, String email){
        Member member = memberService.enrolledUsernameAndEmail(username, email);
        if(member==null){
            return "redirect:/member/findPassword?errorMsg=" + Ut.url.encode("???????????? ?????? ???????????????.");
        }
        return "redirect:/member/findPassword?msg=" + Ut.url.encode("????????? ????????????????????? ?????????????????????.");
    }

    @GetMapping("/charge")
    @PreAuthorize("isAuthenticated()")
    public String charge(@AuthenticationPrincipal MemberContext context, Model model){
        model.addAttribute("member", context);
        return "member/charge";
    }


}
