package com.example.ll.finalproject.article.controller;

import com.example.ll.finalproject.article.dto.request.ArticleForm;
import com.example.ll.finalproject.article.entity.Article;
import com.example.ll.finalproject.article.service.ArticleService;
import com.example.ll.finalproject.security.dto.MemberContext;
import com.example.ll.finalproject.util.Ut;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/post")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @GetMapping("/list")
    public String showList(Model model) {
        List<Article> articles = articleService.getArticles();
        articleService.loadForPrintData(articles);
        for(Article article:articles){
            System.out.println(article);
        }
        model.addAttribute("articles", articles);

        return "article/list";
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/write")
    public String showWrite() {
        return "article/write";
    }

    @GetMapping("/{id}")
    public String showDetail(Model model, @PathVariable Long id) {
        Article article = articleService.getForPrintArticleById(id);
        model.addAttribute("article", article);
        return "article/detail";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/write")
    public String write(@AuthenticationPrincipal MemberContext memberContext, @Valid ArticleForm articleForm) {

        Article article = articleService.write(memberContext.getMember(), articleForm.getSubject(), articleForm.getContent(), articleForm.getHashTagContents());

        String msg = "%d번 게시물이 작성되었습니다.".formatted(article.getId());
        msg = Ut.url.encode(msg);
        return "redirect:/post/%d?msg=%s".formatted(article.getId(), msg);
    }

    @GetMapping("/{id}/delete")
    public String deleteArticle(Model model, @PathVariable Long id) {
        articleService.deleteArticle(id);
        return "redirect:/post/list?msg=" + Ut.url.encode(id+"번 게시글이 삭제되었습니다.");
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/modify")
    public String showModify(@AuthenticationPrincipal MemberContext memberContext, Model model, @PathVariable Long id) {
        Article article = articleService.getForPrintArticleById(id);
        model.addAttribute("article", article);
        return "article/modify";
    }

}
