package com.example.ll.finalproject.withdraw.service;

import com.example.ll.finalproject.member.entity.Member;
import com.example.ll.finalproject.withdraw.dto.request.WithdrawForm;
import com.example.ll.finalproject.withdraw.entity.Withdraw;
import com.example.ll.finalproject.withdraw.repository.WithdrawRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WithdrawService {
    private final WithdrawRepository withdrawRepository;
    public void create(Member member, WithdrawForm withdrawForm) {
        Withdraw withdraw = Withdraw
                .builder()
                .member(member)
                .isPaid(false)
                .bankAccountNo(withdrawForm.getBankAccountNo())
                .bankName(withdrawForm.getBankName())
                .price(withdrawForm.getPrice())
                .build();

        withdrawRepository.save(withdraw);
    }

    public List<Withdraw> findAllApplyList() {
        List<Withdraw> withdrawList = withdrawRepository.findAll();
        return withdrawList;
    }
}
