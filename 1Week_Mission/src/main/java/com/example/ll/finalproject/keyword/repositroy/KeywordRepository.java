package com.example.ll.finalproject.keyword.repositroy;
import com.example.ll.finalproject.keyword.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    Optional<Keyword> findByContent(String keywordContent);
}
