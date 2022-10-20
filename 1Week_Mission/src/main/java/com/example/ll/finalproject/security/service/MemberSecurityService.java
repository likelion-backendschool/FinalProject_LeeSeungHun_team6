package com.example.ll.finalproject.security.service;

import com.example.ll.finalproject.member.entity.Member;
import com.example.ll.finalproject.member.repository.MemberRepository;
import com.example.ll.finalproject.security.dto.MemberContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberSecurityService implements UserDetailsService {
    private final MemberRepository memberRepository;
    //기존에 Member 객체가 아닌 MemberContext를 사용함으로써 컨트로러에서 부가적인 사용자의 정보를 사용가능
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username).get();
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (member.getAuthLevel() == 3) {
            authorities.add(new SimpleGrantedAuthority("member"));
        } else if (member.getAuthLevel() == 7){
            authorities.add(new SimpleGrantedAuthority("admin"));
        }
        return new MemberContext(member,authorities);
    }
}
