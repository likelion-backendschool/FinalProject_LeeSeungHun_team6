package com.example.ll.finalproject.rebate.service;


import com.example.ll.finalproject.base.dto.RsData;
import com.example.ll.finalproject.cash.entity.CashLog;
import com.example.ll.finalproject.member.entity.Member;
import com.example.ll.finalproject.member.servie.MemberService;
import com.example.ll.finalproject.order.entity.OrderItem;
import com.example.ll.finalproject.order.service.OrderService;
import com.example.ll.finalproject.rebate.entity.RebateOrderItem;
import com.example.ll.finalproject.rebate.repository.RebateOrderItemRepository;
import com.example.ll.finalproject.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RebateService {
    private final OrderService orderService;
    private final RebateOrderItemRepository rebateOrderItemRepository;

    private final MemberService memberService;
    @Transactional
    public RsData makeDate(String yearMonth) {
        int monthEndDay = Ut.date.getEndDayOf(yearMonth);

        String fromDateStr = yearMonth + "-01 00:00:00.000000";
        String toDateStr = yearMonth + "-%02d 23:59:59.999999".formatted(monthEndDay);
        LocalDateTime fromDate = Ut.date.parse(fromDateStr);
        LocalDateTime toDate = Ut.date.parse(toDateStr);

        // 데이터 가져오기
        List<OrderItem> orderItems = orderService.findAllByPayDateBetweenOrderByIdAsc(fromDate, toDate);

        // 변환하기
        List<RebateOrderItem> rebateOrderItems = orderItems
                .stream()
                .map(this::toRebateOrderItem)
                .collect(Collectors.toList());

        // 저장하기
        rebateOrderItems.forEach(this::makeRebateOrderItem);

        return RsData.of("S-1", "정산데이터가 성공적으로 생성되었습니다.");
    }
    public RebateOrderItem toRebateOrderItem(OrderItem orderItem) {
        return new RebateOrderItem(orderItem);
    }
    @Transactional
    public void makeRebateOrderItem(RebateOrderItem item) {
        RebateOrderItem oldRebateOrderItem = rebateOrderItemRepository.findByOrderItemId(item.getOrderItem().getId()).orElse(null);

        if (oldRebateOrderItem != null) {
            rebateOrderItemRepository.delete(oldRebateOrderItem);
        }

        rebateOrderItemRepository.save(item);
    }

    public List<RebateOrderItem> findRebateOrderItemsByPayDateIn(String yearMonth) {
        int monthEndDay = Ut.date.getEndDayOf(yearMonth);

        String fromDateStr = yearMonth + "-01 00:00:00.000000";
        String toDateStr = yearMonth + "-%02d 23:59:59.999999".formatted(monthEndDay);

        LocalDateTime fromDate = Ut.date.parse(fromDateStr);
        LocalDateTime toDate = Ut.date.parse(toDateStr);

        return rebateOrderItemRepository.findAllByPayDateBetweenOrderByIdAsc(fromDate, toDate);
    }

    @Transactional
    public RsData rebate(long orderItemId) {
        RebateOrderItem rebateOrderItem = rebateOrderItemRepository.findByOrderItemId(orderItemId).get();

        Member admin = memberService.findByAuthLevel(7);

        if (rebateOrderItem.isRebateAvailable() == false) {
            return RsData.of("F-1", "정산을 할 수 없는 상태입니다.");
        }

        int calculateRebatePrice = rebateOrderItem.calculateRebatePrice();

        CashLog cashLogToMember = memberService.addCash(
                rebateOrderItem.getProduct().getAuthor(),
                calculateRebatePrice,
                "정산__%d__지급__예치금".formatted(rebateOrderItem.getOrderItem().getId())
        ).getData().getCashLog();

        CashLog cashLogToAdmin = memberService.addCash(
                admin,
                calculateRebatePrice,
                "정산__%d__지급__관리자".formatted(rebateOrderItem.getOrderItem().getId())
        ).getData().getCashLog();


        rebateOrderItem.setRebateDone(cashLogToMember.getId());

        return RsData.of(
                "S-1",
                "주문품목번호 %d번에 대해서 판매자와 관리자에게 %s원씩 정산을 완료하였습니다.".formatted(rebateOrderItem.getOrderItem().getId(), calculateRebatePrice),
                Ut.mapOf(
                        "cashLogId", cashLogToMember.getId()
                )
        );
    }
}
