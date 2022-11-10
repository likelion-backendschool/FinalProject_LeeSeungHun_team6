package com.example.ll.finalproject.app.base.initData;

import com.example.ll.finalproject.app.member.entity.Member;
import com.example.ll.finalproject.app.member.service.MemberService;
import com.example.ll.finalproject.app.myBook.service.MyBookService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("dev") //dev가 활성화되었을 때 컨테이너에 등록되는 빈 정의
public class DevInitData {
    @Bean
    CommandLineRunner initData(MemberService memberService, MyBookService mybookService, PasswordEncoder passwordEncoder) {
        String password = passwordEncoder.encode("1234");
        return args -> {
            Member member1 = memberService.join("user1", password, "user1@test.com");
            Member member2 = memberService.join("user2", password, "user2@test.com");

            mybookService.write(member1, "제목 1", "내용 1");
            mybookService.write(member1, "제목 2", "내용 2");
            mybookService.write(member2, "제목 3", "내용 3");
            mybookService.write(member2, "제목 4", "내용 4");
        };
    }
}
