package com.example.ll.finalproject.app.security.dto;

import com.example.ll.finalproject.app.member.entity.Member;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class MemberContext extends User {
    private final Long id;
    @Setter
    private LocalDateTime modifyDate;
    private final String username;
    @Setter
    private String email;
    @Setter
    private String nickname;

    @Setter
    private long restCash;

    @Getter
    @Setter
    private List<GrantedAuthority> authorities;

    public MemberContext(Member member, List<GrantedAuthority> authorities) {
        super(member.getUsername(), member.getPassword(), authorities);
        this.id = member.getId();
        this.modifyDate = member.getModifyDate();
        this.username = member.getUsername();
        this.email = member.getEmail();
        this.nickname = member.getNickname();
        this.restCash = member.getRestCash();
        this.authorities = authorities;
    }

    public Member getMember() {
        return Member
                .builder()
                .id(id)
                .modifyDate(modifyDate)
                .username(username)
                .email(email)
                .nickname(nickname)
                .restCash(restCash)
                .build();
    }

    public String getName() {
        return getUsername();
    }
    public boolean memberIs(Member member) {
        return id.equals(member.getId());
    }
    public boolean memberIsNot(Member member) {
        return memberIs(member) == false;
    }

}
