package com.example.ll.finalproject.base.initData;


import com.example.ll.finalproject.article.entity.Article;
import com.example.ll.finalproject.article.service.ArticleService;
import com.example.ll.finalproject.cart.entity.CartItem;
import com.example.ll.finalproject.cart.service.CartService;
import com.example.ll.finalproject.keyword.entity.Keyword;
import com.example.ll.finalproject.keyword.servcice.KeywordService;
import com.example.ll.finalproject.member.entity.Member;
import com.example.ll.finalproject.member.servie.MemberService;
import com.example.ll.finalproject.mybook.service.MyBookService;
import com.example.ll.finalproject.order.entity.Order;
import com.example.ll.finalproject.order.service.OrderService;
import com.example.ll.finalproject.product.entity.Product;
import com.example.ll.finalproject.product.service.ProductService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface InitDataBefore {
    default void before(MemberService memberService, ArticleService articleService, ProductService productService, CartService cartService, OrderService orderService, MyBookService myBookService){
        class Helper {
            public Order order(Member member, List<Product> products) {
                for (int i = 0; i < products.size(); i++) {
                    Product product = products.get(i);

                    cartService.addItem(member, product);
                }

                return orderService.createFromCart(member);
            }
        }
        Helper helper = new Helper();

        String password = "1234";
        Member member1 = memberService.join("user1", password, "user1@test.com", "nickname1");
        Member member2 = memberService.join("user2", password, "user2@test.com", "nickname2");
        Member member3 = memberService.join("user3", password, "user3@test.com", "nickname3");

        Article article1 = articleService.write(member1, "제목1", "내용1", "<p>내용1</p>","#자바 #프로그래밍");
        Article article2 = articleService.write(member2, "제목2", "내용2", "<p>내용2</p>","#HTML #프로그래밍");


        List<Article> articleList = new ArrayList<>();
        articleList.add(article1);
        articleList.add(article2);

        Product product1 = productService.create(member3, "제목1", 2_000, "#자바1 #프로그래밍1", articleList);
        Product product2 = productService.create(member3, "제목2", 4_000, "#자바2 #프로그래밍2", articleList);
        Product product3 = productService.create(member3, "제목3", 5_000, "#자바3 #프로그래밍3", articleList);
        Product product4 = productService.create(member3, "제목4", 6_000, "#자바4 #프로그래밍4", articleList);

//        myBookService.addProduct(member1, product1);
//        myBookService.addProduct(member1, product2);
//        myBookService.addProduct(member1, product3);
//        myBookService.addProduct(member1, product4);


        memberService.addCash(member1, 10_000, "충전__무통장입금");
        memberService.addCash(member1, 20_000, "충전__무통장입금");
        memberService.addCash(member1, -5_000, "출금__일반");
        memberService.addCash(member1, 1_000_000, "충전__무통장입금");
        memberService.addCash(member2, 2_000_000, "충전__무통장입금");

        // 1번 주문 : 결제완료
        Order order1 = helper.order(member1, Arrays.asList(
                        product1,
                        product2
                )
        );

        orderService.payByRestCashOnly(order1);
        myBookService.addProduct(member1, Arrays.asList(
                product1,
                product2
        ));

        // 2번 주문 : 결제 후 환불
        Order order2 = helper.order(member2, Arrays.asList(
                        product3,
                        product4
                )
        );

        orderService.payByRestCashOnly(order2);

        orderService.refund(order2);


    }
}
