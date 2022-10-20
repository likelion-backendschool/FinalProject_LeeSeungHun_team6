package com.example.ll.finalproject.productTag.repository;

import com.example.ll.finalproject.productTag.entity.ProductTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductTagRepository extends JpaRepository<ProductTag, Long> {
    Optional<ProductTag> findByIdAndKeywordId(Long productId, Long keywordId);
    List<ProductTag> findAllByProductId(Long articleId);
    List<ProductTag> findAllByProductIdIn(long[] ids);

}
