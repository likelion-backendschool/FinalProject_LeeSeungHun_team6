package com.example.ll.finalproject.service;

import com.example.ll.finalproject.app.keyword.entity.Keyword;
import com.example.ll.finalproject.app.keyword.servcice.KeywordService;
import com.example.ll.finalproject.app.member.entity.Member;
import com.example.ll.finalproject.app.member.service.MemberService;
import com.example.ll.finalproject.app.product.entity.Product;
import com.example.ll.finalproject.app.product.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class ProductServiceTests {
    @Autowired
    private MemberService memberService;

    @Autowired
    private ProductService productService;

    @Autowired
    private KeywordService keywordService;

    @Test
    @DisplayName("상품 등록")
    void t1() {
        Member member = memberService.findById(1L);
        Keyword keyword = keywordService.save("수정키워드");

//        Product product = productService.create(member, "수정제목", 2_000, keyword);

//        assertThat(product).isNotNull();
//        assertThat(product.getSubject()).isEqualTo("수정제목");
//        assertThat(product.getPrice()).isEqualTo(2_000);
    }

    @Test
    @DisplayName("상품 수정")
    void t2() {
        Product product1 = productService.findById(1L);
        productService.modify(product1, "수정키워드", 4_000);

        assertThat(product1).isNotNull();
        assertThat(product1.getSubject()).isEqualTo("수정키워드");
        assertThat(product1.getPrice()).isEqualTo(4_000);
    }

}
