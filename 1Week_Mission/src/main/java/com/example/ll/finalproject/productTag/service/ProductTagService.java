package com.example.ll.finalproject.productTag.service;


import com.example.ll.finalproject.keyword.entity.Keyword;
import com.example.ll.finalproject.keyword.servcice.KeywordService;
import com.example.ll.finalproject.product.entity.Product;
import com.example.ll.finalproject.productTag.entity.ProductTag;
import com.example.ll.finalproject.productTag.repository.ProductTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductTagService {
    private final KeywordService keywordService;
    private final ProductTagRepository productTagRepository;
    public List<ProductTag> getProductTagsByArticleIdIn(long[] ids) {
        return productTagRepository.findAllByProductIdIn(ids);
    }

    public void applyProductTags(Product product, String productContents) {
        List<ProductTag> oldProductTags= getProductTags(product);

        List<String> keywordContents = Arrays.stream(productContents.split("#"))
                .map(String :: trim)
                .filter(s -> s.length() >0)
                .collect(Collectors.toList());


        List<ProductTag> needToDelete = new ArrayList<>();

        for (ProductTag oldProductTag : oldProductTags) {
            boolean contains = keywordContents.stream().anyMatch(s -> s.equals(oldProductTag.getKeyword().getContent()));

            if (contains == false) {
                needToDelete.add(oldProductTag);
            }
        }

        needToDelete.forEach(productTag -> {
            productTagRepository.delete(productTag);
        });


        keywordContents.forEach(keywordContent -> {
            saveProductTag(product, keywordContent);
        });

    }
    private ProductTag saveProductTag(Product product, String keywordContent) {
        Keyword keyword = keywordService.save(keywordContent);
        Optional<ProductTag> opProductTag = productTagRepository.findByIdAndKeywordId(product.getId(), keyword.getId());
        if(opProductTag.isPresent()){
            return opProductTag.get();
        }
        ProductTag productTag = ProductTag.builder()
                .product(product)
                .keyword(keyword)
                .build();

        productTagRepository.save(productTag);

        return productTag;
    }
    public List<ProductTag> getProductTags(Product product) {
        return productTagRepository.findAllByProductId(product.getId());
    }

    public void deleteProductTag(List<ProductTag> productTags) {
        for(ProductTag productTag: productTags){
            productTagRepository.delete(productTag);
        }
    }
}

