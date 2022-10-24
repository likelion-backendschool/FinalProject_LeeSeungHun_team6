package com.example.ll.finalproject.member.controller;

import com.example.ll.finalproject.member.dto.request.JoinForm;
import com.example.ll.finalproject.member.entity.Member;
import com.example.ll.finalproject.member.servie.MemberService;
import com.example.ll.finalproject.security.dto.MemberContext;
import com.example.ll.finalproject.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        if (uri != null && !uri.contains("/member")) {
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
            return "redirect:/member/registerAuthor?errorMsg=" + Ut.url.encode("닉네임을 입력해주세요.");
        }
        Member member = memberService.registerAuthor(memberContext, nickname);
        // 기존에 세션에 저장된 MemberContext 객체의 내용을 수정하는 코드 시작
        Authentication authentication = new UsernamePasswordAuthenticationToken(memberContext, member.getPassword(), memberContext.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 기존에 세션에 저장된 MemberContext 객체의 내용을 수정하는 코드 끝
        return "redirect:/product/list?msg=" + Ut.url.encode("작가명이 등록되었습니다.");
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

        return "redirect:/member/login?msg=" + Ut.url.encode("회원가입이 완료되었습니다.");
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

        // 기존에 세션에 저장된 MemberContext 객체의 내용을 수정하는 코드 시작
        memberContext.setModifyDate(member.getModifyDate());
        memberContext.setEmail(member.getEmail());
        memberContext.setNickname(member.getNickname());
        Authentication authentication = new UsernamePasswordAuthenticationToken(memberContext, member.getPassword(), memberContext.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 기존에 세션에 저장된 MemberContext 객체의 내용을 수정하는 코드 끝

        return "redirect:/member/modify?msg=" + Ut.url.encode("수정이 완료되었습니다.");
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
            return "redirect:/member/modifyPassword?errorMsg=" + Ut.url.encode("현재 비밀번호가 일치하지 않습니다.");
        }
        memberService.modifyPassword(member, password);

        return "redirect:/member/modifyPassword?msg=" + Ut.url.encode("비밀번호 수정이 완료되었습니다.");
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
            return "redirect:/member/findUsername?errorMsg=" + Ut.url.encode("존재하지 않는 이메일입니다.");
        }
        return "redirect:/member/findUsername?msg=" + Ut.url.encode("아이디: "+memberUsername);
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
            return "redirect:/member/findPassword?errorMsg=" + Ut.url.encode("일치하지 않는 정보입니다.");
        }
        return "redirect:/member/findPassword?msg=" + Ut.url.encode("메일로 임시비밀번호가 발송되었습니다.");
    }
}
