package com.example.ll.finalproject.withdraw.entity;

import com.example.ll.finalproject.base.entity.BaseEntity;
import com.example.ll.finalproject.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class Withdraw extends BaseEntity {
    @ManyToOne
    private Member member;

    private boolean isPaid; // 출금 여부

    private long price;

    private String bankName;

    private int bankAccountNo;


}
