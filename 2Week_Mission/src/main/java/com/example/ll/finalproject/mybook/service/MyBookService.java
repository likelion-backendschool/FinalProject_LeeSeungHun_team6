package com.example.ll.finalproject.mybook.service;

import com.example.ll.finalproject.member.entity.Member;
import com.example.ll.finalproject.mybook.entity.MyBook;
import com.example.ll.finalproject.mybook.repository.MyBookRepository;
import com.example.ll.finalproject.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyBookService {
    private final MyBookRepository myBookRepository;
    public void addProduct(Member actor, List<Product> products) {
        for(Product product:products){
            //매번 시도 안좋음 리팩토링**
            MyBook myBook = myBookRepository.findByMemberIdAndProductId(actor.getId(), product.getId()).orElse(null);

            if(myBook==null){
                myBook = MyBook.builder()
                        .product(product)
                        .member(actor)
                        .number(1)
                        .build();
            }
            else{
                myBook.setNumber(myBook.getNumber()+1);
            }
            myBookRepository.save(myBook);
        }
    }

    public void addProduct(Member member, Product product){
        //기존에 있는 상품인지 체크
        MyBook myBook = myBookRepository.findByMemberIdAndProductId(member.getId(), product.getId()).orElse(null);
        if(myBook==null){
            myBook = MyBook.builder()
                    .product(product)
                    .member(member)
                    .number(1)
                    .build();
        }
        else{
            myBook.setNumber(myBook.getNumber()+1);
        }
        myBookRepository.save(myBook);
    }

    public List<MyBook> findAllByMemberId(Long id) {
        List<MyBook> myBooks = myBookRepository.findAllByMemberId(id);
        if(myBooks==null){
            return null;
        }
        return myBooks;
    }

    public boolean isExisted(Member member, Product product) {
        MyBook myBook = myBookRepository.findByMemberIdAndProductId(member.getId(),product.getId()).orElse(null);
        if(myBook==null){
            return false;
        }
        return true;
    }
}
