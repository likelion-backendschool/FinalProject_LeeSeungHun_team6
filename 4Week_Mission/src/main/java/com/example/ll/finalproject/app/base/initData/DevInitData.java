package com.example.ll.finalproject.app.base.initData;

import com.example.ll.finalproject.app.article.service.ArticleService;
import com.example.ll.finalproject.app.cart.service.CartService;
import com.example.ll.finalproject.app.member.servie.MemberService;
import com.example.ll.finalproject.app.mybook.service.MyBookService;
import com.example.ll.finalproject.app.order.service.OrderService;
import com.example.ll.finalproject.app.product.service.ProductService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class DevInitData implements InitDataBefore{
    private boolean initDataDone = false;
    @Bean
    CommandLineRunner initData(MemberService memberService, ArticleService articleService, ProductService productService, CartService cartService, OrderService orderService, MyBookService myBookService) {
        return args -> {
            if (initDataDone) return;

            before(memberService, articleService, productService,cartService, orderService, myBookService);

            initDataDone = true;
        };
    }
}
