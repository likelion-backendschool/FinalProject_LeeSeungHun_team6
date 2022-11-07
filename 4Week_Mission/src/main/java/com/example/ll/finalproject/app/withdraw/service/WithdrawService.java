package com.example.ll.finalproject.app.withdraw.service;

import com.example.ll.finalproject.app.member.entity.Member;
import com.example.ll.finalproject.app.member.servie.MemberService;
import com.example.ll.finalproject.app.withdraw.dto.request.WithdrawForm;
import com.example.ll.finalproject.app.withdraw.entity.Withdraw;
import com.example.ll.finalproject.app.withdraw.repository.WithdrawRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WithdrawService {
    private final WithdrawRepository withdrawRepository;

    private final MemberService memberService;
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

    public Withdraw findById(long id) {
        Withdraw withdraw = withdrawRepository.findById(id).orElse(null);
        return withdraw;
    }

    public void apply(Withdraw withdraw) {
        memberService.addCash(withdraw.getMember(), withdraw.getPrice() * -1, "출금__일반");
        withdraw.setPaid(true);
        withdrawRepository.save(withdraw);
    }
}
