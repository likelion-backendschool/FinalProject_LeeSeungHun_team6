package com.example.ll.finalproject.member.servie;

import com.example.ll.finalproject.member.entity.Member;
import com.example.ll.finalproject.member.exception.AlreadyJoinException;
import com.example.ll.finalproject.member.repository.MemberRepository;
import com.example.ll.finalproject.security.dto.MemberContext;
import lombok.RequiredArgsConstructor;
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

    public void modify(MemberContext memberContext, Member member, String email, String nickname) {
        member.setEmail(email);
        member.setNickname(nickname);
        memberRepository.save(member);

        memberContext.setModifyDate(member.getModifyDate());
        memberContext.setEmail(member.getEmail());
        memberContext.setNickname(member.getNickname());
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
}
