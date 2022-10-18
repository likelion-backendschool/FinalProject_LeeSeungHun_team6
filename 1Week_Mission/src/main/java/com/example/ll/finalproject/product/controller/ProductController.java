package com.example.ll.finalproject.product.controller;


import com.example.ll.finalproject.article.entity.Article;
import com.example.ll.finalproject.article.service.ArticleService;
import com.example.ll.finalproject.hashTag.entity.HashTag;
import com.example.ll.finalproject.hashTag.service.HashTagService;
import com.example.ll.finalproject.keyword.entity.Keyword;
import com.example.ll.finalproject.keyword.servcice.KeywordService;
import com.example.ll.finalproject.member.entity.Member;
import com.example.ll.finalproject.product.entity.Product;
import com.example.ll.finalproject.product.service.ProductService;
import com.example.ll.finalproject.security.dto.MemberContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


import java.util.List;


@Controller
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;
    private final KeywordService keywordService;
    private final ArticleService articleService;
    private final HashTagService hashTagService;
    @GetMapping("/list")
    public String showProductList(Model model) {
        List<Product> products = productService.getProducts();
        model.addAttribute("products", products);
        return "product/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String showProductCreate(@AuthenticationPrincipal MemberContext memberContext, Model model) {
        Member actor = memberContext.getMember();
        List<Article> articles = articleService.findAllByMemberId(actor.getId());
        List<HashTag> hashTags = hashTagService.getHashTags(articles);

        List<Keyword> keywords =keywordService.getKeywordByHashTag(hashTags);
        model.addAttribute("keywords", keywords);
        return "product/create";
    }
}
