package com.example.ll.finalproject.app.myBook.entity;

import com.example.ll.finalproject.app.base.entity.BaseEntity;
import com.example.ll.finalproject.app.member.entity.Member;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class MyBook extends BaseEntity {
    @ManyToOne
    private Member author;

    private String subject;

    private String content;

}
