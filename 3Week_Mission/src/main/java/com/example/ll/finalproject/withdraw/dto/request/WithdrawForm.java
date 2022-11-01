package com.example.ll.finalproject.withdraw.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
@Setter
@Getter
public class WithdrawForm {
    @NotEmpty
    private String bankName;
    @NotEmpty
    private int bankAccountNo;
    @NotEmpty
    private long price;
}
