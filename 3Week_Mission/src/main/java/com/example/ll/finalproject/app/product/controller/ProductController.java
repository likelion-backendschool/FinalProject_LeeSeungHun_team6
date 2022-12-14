package com.example.ll.finalproject.app.product.controller;


import com.example.ll.finalproject.app.article.entity.Article;
import com.example.ll.finalproject.app.article.service.ArticleService;
import com.example.ll.finalproject.app.member.entity.Member;
import com.example.ll.finalproject.app.mybook.entity.MyBook;
import com.example.ll.finalproject.app.mybook.service.MyBookService;
import com.example.ll.finalproject.app.product.dto.request.ProductForm;
import com.example.ll.finalproject.app.product.entity.Product;
import com.example.ll.finalproject.app.product.exception.ActorCanNotSeeProductDetail;
import com.example.ll.finalproject.app.product.service.ProductInterArticleService;
import com.example.ll.finalproject.app.product.service.ProductService;
import com.example.ll.finalproject.app.security.dto.MemberContext;
import com.example.ll.finalproject.app.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;


@Controller
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;
    private final ArticleService articleService;

    private final ProductInterArticleService productInterArticleService;
    private final MyBookService myBookService;
    @GetMapping("/list")
    @PreAuthorize("isAuthenticated()")
    public String showProductList(Model model, @AuthenticationPrincipal MemberContext memberContext) {
        List<Product> products = productService.getProducts();
        model.addAttribute("products", products);
        return "product/list";
    }

    //?????? ??????????????? ????????? ????????????
    @GetMapping("/list/myProduct")
    public String myProduct(@AuthenticationPrincipal MemberContext memberContext, Model model){
        Member member = memberContext.getMember();

        List<MyBook> myBooks = myBookService.findAllByMemberId(member.getId());

        model.addAttribute("myBooks", myBooks);
        return "product/myProduct";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String showProductCreate(@AuthenticationPrincipal MemberContext memberContext, Model model) {
        if(memberContext.getNickname()==null){
            return "redirect:/member/registerAuthor?errorMsg=" + Ut.url.encode("???????????? ??????????????????.");
        }
        Member member = memberContext.getMember();
        List<Article> articles = articleService.findAllByMemberId(member.getId());
        model.addAttribute("articles",articles);
        return "product/create";
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String write(@AuthenticationPrincipal MemberContext memberContext, @Valid ProductForm productForm, BindingResult bindingResult) {
        Member member = memberContext.getMember();

        if(bindingResult.hasErrors()){
            return "redirect:/product/create?warningMsg=" + Ut.url.encode("????????? ??????????????????.");
        }
        List<Article> articles = articleService.getArticleById(productForm.getArticleId());

        Product product = productService.create(member, productForm.getSubject(), productForm.getPrice(), productForm.getProductTagContents(), articles);
        myBookService.addProduct(member, product);

        String msg = "%d??? ????????? ?????????????????????.".formatted(product.getId());
        msg = Ut.url.encode(msg);
        return "redirect:/product/%d?msg=%s".formatted(product.getId(), msg);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public String showProductDetail(@AuthenticationPrincipal MemberContext memberContext, Model model, @PathVariable Long id) {
        Member member = memberContext.getMember();
        //????????? ????????? ??? ?????? ??????
        Product product = productService.getLoadForPrintProduct(id);
        if(!myBookService.isExisted(member,product)){
            throw new ActorCanNotSeeProductDetail();
        }
        List<Article> articles = productInterArticleService.getProductInterArticle(id);
        model.addAttribute("product", product);
        model.addAttribute("articles", articles);
        return "product/detail";
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/delete")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/product/list?msg=" + Ut.url.encode(id+"??? ????????? ?????????????????????.");
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/modify")
    public String showModify(@AuthenticationPrincipal MemberContext memberContext, Model model, @PathVariable Long id) {
        Product product = productService.getLoadForPrintProduct(id);
        List<Article> articles = productInterArticleService.getProductInterArticle(id);
        if (memberContext.memberIsNot(product.getAuthor())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        model.addAttribute("product", product);
        model.addAttribute("articles", articles);
        return "product/modify";
    }
    @PostMapping("/{id}/modify")
    public String modify(@AuthenticationPrincipal MemberContext memberContext, @PathVariable Long id, @Valid ProductForm productForm) {
        Product product = productService.findById(id);
        if (memberContext.memberIsNot(product.getAuthor())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        productService.modify(product, productForm.getSubject(), productForm.getPrice());

        String msg = Ut.url.encode("%d??? ???????????? ?????????????????????.".formatted(id));
        return "redirect:/product/%d?msg=%s".formatted(id, msg);
    }
}
