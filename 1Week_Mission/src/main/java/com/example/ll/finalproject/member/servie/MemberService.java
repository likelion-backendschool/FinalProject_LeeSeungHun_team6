package com.example.ll.finalproject.member.servie;

import com.example.ll.finalproject.member.entity.Member;
import com.example.ll.finalproject.member.exception.AlreadyJoinException;
import com.example.ll.finalproject.member.repository.MemberRepository;
import com.example.ll.finalproject.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    private final JavaMailSender javaMailSender;

    public Member join(String username, String password, String email, String nickname) {
        if (memberRepository.findByUsername(username).isPresent()) {
            throw new AlreadyJoinException();
        }

        Member member = Member.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .nickname(nickname)
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

    public Member enrolledUsernameAndEmail(String username, String email) {
        Member memberUsername = memberRepository.findByUsername(username).orElse(null);
        Member memberEmail = memberRepository.findByEmail(email).orElse(null);
        if(memberUsername==null || memberEmail==null || !(memberUsername.equals(memberEmail))){
            return null;
        }
        sendEmailRandomPassword(memberEmail);
        return memberUsername;
    }
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
}
