package com.example.ll.finalproject.home;

import com.example.ll.finalproject.article.entity.Article;
import com.example.ll.finalproject.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final ArticleService articleService;

    @GetMapping
    public String home(Model model){
        List<Article> articles = articleService.getRecentArticles();
        articleService.loadForPrintData(articles);

        model.addAttribute("articles", articles);
        return "index";
    }
}
