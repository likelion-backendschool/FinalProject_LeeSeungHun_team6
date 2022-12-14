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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/post")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @GetMapping("/list")
    public String showList(Model model, @RequestParam(required = false)String kwType, @RequestParam(required = false)String kw, @AuthenticationPrincipal MemberContext memberContext) {
        if(kw!=null){
            List<Article> articles = articleService.search(kwType,kw, memberContext);
            model.addAttribute("articles", articles);
            return "article/list";
        }
        List<Article> articles = articleService.getRecentArticles();
        articleService.loadForPrintData(articles);
        model.addAttribute("articles", articles);
        return "article/list";
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/write")
    public String showWrite() {
        return "article/write";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public String showDetail(Model model, @PathVariable Long id) {
        Article article = articleService.getForPrintArticleById(id);
        model.addAttribute("article", article);
        return "article/detail";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/write")
    public String write(@AuthenticationPrincipal MemberContext memberContext, @Valid ArticleForm articleForm, BindingResult bindingResult) {

        if(bindingResult.hasErrors()){
            return "redirect:/post/write?warningMsg=" + Ut.url.encode("????????? ??????????????????.");
        }
        String articleFormContentHtml =articleForm.getContent();
        String articleGetContent = articleService.getContentFromContentHtml(articleFormContentHtml);
        Article article = articleService.write(memberContext.getMember(), articleForm.getSubject(), articleGetContent,articleFormContentHtml, articleForm.getHashTagContents());

        String msg = "%d??? ???????????? ?????????????????????.".formatted(article.getId());
        msg = Ut.url.encode(msg);
        return "redirect:/post/%d?msg=%s".formatted(article.getId(), msg);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/delete")
    public String deleteArticle(Model model, @PathVariable Long id) {
        articleService.deleteArticle(id);
        return "redirect:/post/list?msg=" + Ut.url.encode(id+"??? ???????????? ?????????????????????.");
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/modify")
    public String showModify(@AuthenticationPrincipal MemberContext memberContext, Model model, @PathVariable Long id) {
        Article article = articleService.getForPrintArticleById(id);
        if (memberContext.memberIsNot(article.getAuthor())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        model.addAttribute("article", article);
        return "article/modify";
    }

    @PostMapping("/{id}/modify")
    public String modify(@AuthenticationPrincipal MemberContext memberContext, @PathVariable Long id, @Valid ArticleForm articleForm, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            return "redirect:/post/"+id+"/modify?warningMsg=" + Ut.url.encode("????????? ??????????????????.");
        }
        Article article = articleService.getForPrintArticleById(id);

        if (memberContext.memberIsNot(article.getAuthor())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        //???????????????(html)??? articleFormContentHtml??? ???????????? getContentFromContentHtml ????????? ?????? ????????? ????????????.
        String articleFormContentHtml =articleForm.getContent();
        String articleGetContent = articleService.getContentFromContentHtml(articleFormContentHtml);

        articleService.modify(article, articleForm.getSubject(), articleGetContent, articleFormContentHtml,articleForm.getHashTagContents());

        String msg = Ut.url.encode("%d??? ???????????? ?????????????????????.".formatted(id));
        return "redirect:/post/%d?msg=%s".formatted(id, msg);
    }

}
