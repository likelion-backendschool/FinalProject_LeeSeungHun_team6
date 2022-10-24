package com.example.ll.finalproject.member.servie;

import com.example.ll.finalproject.base.dto.RsData;
import com.example.ll.finalproject.cash.entity.CashLog;
import com.example.ll.finalproject.cash.service.CashService;
import com.example.ll.finalproject.member.entity.Member;
import com.example.ll.finalproject.member.exception.AlreadyJoinException;
import com.example.ll.finalproject.member.repository.MemberRepository;
import com.example.ll.finalproject.security.dto.MemberContext;
import com.example.ll.finalproject.util.Ut;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    private final JavaMailSender javaMailSender;
    private final CashService cashService;

    public Member join(String username, String password, String email, String nickname) {
        if (memberRepository.findByUsername(username).isPresent()) {
            throw new AlreadyJoinException();
        }

        Member member = Member.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .nickname(nickname)
                .authLevel(4)
                .build();

        memberRepository.save(member);

        return member;
    }
    public Member join(String username, String password, String email) {
        if (memberRepository.findByUsername(username).isPresent()) {
            throw new AlreadyJoinException();
        }

        Member member = Member.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .authLevel(3)
                .build();

        memberRepository.save(member);

        return member;
    }


    @Transactional(readOnly = true)
    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    public Member getMemberById(Long loginedMemberId) {
        Member member = memberRepository.findById(loginedMemberId).orElse(null);
        return member;
    }

    public void modify(Member member, String email, String nickname) {
        member.setEmail(email);
        member.setNickname(nickname);
        memberRepository.save(member);
    }
    public void modifyPassword(Member member,String password) {
        member.setPassword(passwordEncoder.encode(password));
        memberRepository.save(member);
    }

    public boolean checkPassword(Member member, String oldPassword) {
        if(passwordEncoder.matches(oldPassword, member.getPassword())){
            return true;
        }
        return false;
    }

    public String enrolledEmail(String email) {
        Member member = memberRepository.findByEmail(email).orElse(null);
        if(member==null){
            return null;
        }
        return member.getUsername();
    }

    //등록된 아이디와 이메일인지, 동일한 계정인지 확인
    public Member enrolledUsernameAndEmail(String username, String email) {
        Member member = memberRepository.findByUsernameAndEmail(username,email).orElse(null);
        if(member==null){
            return null;
        }
        sendEmailRandomPassword(member);
        return member;
    }
    
    //패스워드 분실 시 임시 비밀번호 발급
    public void sendEmailRandomPassword(Member member){
        String randomPassword = Ut.randomPassword();
        member.setPassword(passwordEncoder.encode(randomPassword));
        memberRepository.save(member);

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(member.getEmail());
        simpleMailMessage.setSubject("임시 비밀번호 발급");
        simpleMailMessage.setText(randomPassword);
        javaMailSender.send(simpleMailMessage);
    }

    public Member findById(long id) {
        Member member = memberRepository.findById(id).orElse(null);
        return member;
    }

    public Member registerAuthor(MemberContext memberContext, String nickname) {
        Member member = memberRepository.findById(memberContext.getId()).orElse(null);
        member.setNickname(nickname);
        member.setAuthLevel(4);
        List<GrantedAuthority> authorities = memberContext.getAuthorities();
        authorities.remove(0);
        authorities.add(new SimpleGrantedAuthority("author"));
        memberContext.setAuthorities(authorities);
        memberContext.setNickname(member.getNickname());
        memberRepository.save(member);
        return member;
    }

    @Transactional
    public RsData<AddCashRsDataBody> addCash(Member member, long price, String eventType) {
        CashLog cashLog = cashService.addCash(member, price, eventType);

        long newRestCash = member.getRestCash() + cashLog.getPrice();
        member.setRestCash(newRestCash);
        memberRepository.save(member);

        return RsData.of(
                "S-1",
                "성공",
                new AddCashRsDataBody(cashLog, newRestCash)
        );
    }
    @Data
    @AllArgsConstructor
    public static class AddCashRsDataBody {
        CashLog cashLog;
        long newRestCash;
    }
}
