package com.example.ll.finalproject.rebate.controller;

import com.example.ll.finalproject.base.dto.RsData;
import com.example.ll.finalproject.rebate.entity.RebateOrderItem;
import com.example.ll.finalproject.rebate.service.RebateService;
import com.example.ll.finalproject.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/adm/rebate")
@RequiredArgsConstructor
public class AdmRebateController {
    private final RebateService rebateService;
    @GetMapping("/adminHome")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String adminHome() {
        return "adm/home/main";
    }

    @GetMapping("/makeData")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String showMakeData() {
        return "adm/rebate/makeData";
    }

    @PostMapping("/makeData")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String makeData(String yearMonth) {
        RsData makeDateRsData = rebateService.makeDate(yearMonth);

        String redirect = makeDateRsData.addMsgToUrl("redirect:/adm/rebate/rebateOrderItemList?yearMonth=" + yearMonth);

        return redirect;
    }

    @GetMapping("/rebateOrderItemList")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String showRebateOrderItemList(String yearMonth, Model model) {
        if (yearMonth == null) {
            yearMonth = "2022-10";
        }
        List<RebateOrderItem> items = rebateService.findRebateOrderItemsByPayDateIn(yearMonth);

        model.addAttribute("items", items);

        return "adm/rebate/rebateOrderItemList";
    }

    @PostMapping("/rebateOne/{orderItemId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String rebateOne(@PathVariable long orderItemId, HttpServletRequest req) {
        RsData rebateRsData = rebateService.rebate(orderItemId);

        String referer = req.getHeader("Referer");

        String yearMonth = Ut.url.getQueryParamValue(referer, "yearMonth", "");

        String redirect = "redirect:/adm/rebate/rebateOrderItemList?yearMonth=" + yearMonth;

        redirect = rebateRsData.addMsgToUrl(redirect);

        return redirect;
    }
    @PostMapping("/rebate")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String rebate(String ids, HttpServletRequest req) {

        String[] idsArr = ids.split(",");

        Arrays.stream(idsArr)
                .mapToLong(Long::parseLong)
                .forEach(id -> {
                    rebateService.rebate(id);
                });

        String referer = req.getHeader("Referer");
        System.out.println("dd");
        System.out.println(referer);
        System.out.println("zz");
        String yearMonth = Ut.url.getQueryParamValue(referer, "yearMonth", "");

        String redirect = "redirect:/adm/rebate/rebateOrderItemList?yearMonth=" + yearMonth;
        redirect += "&msg=" + Ut.url.encode("%d건의 정산품목을 정산처리하였습니다.".formatted(idsArr.length));

        return redirect;
    }
}
