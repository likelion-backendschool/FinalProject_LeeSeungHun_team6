package com.example.ll.finalproject.job.rebateOrderItem;

import com.example.ll.finalproject.app.order.entity.OrderItem;
import com.example.ll.finalproject.app.order.repository.OrderItemRepository;
import com.example.ll.finalproject.app.rebate.entity.RebateOrderItem;
import com.example.ll.finalproject.app.rebate.repository.RebateOrderItemRepository;
import com.example.ll.finalproject.app.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

//잡은 여러가지 스텝, 스텝은 여러가지 테스클릿이나 Chunks(아이템처리자)로 나누어져서 실행
//아이템 처리자는 Reader(읽기), Processor(변환 작업), Writer(쓰기)


//기존적으로 @Bean을 붙이면 싱글톤 = > 스프링부트앱이 꺼지기 전까지 살아 있음 , 빈들이 다 객체화 되어 저장됨, 공유 자원임
//더 수명이 짧은 건 세션 @SessionScope => 처음 접근 시 세션이 활성화 됨, 세션이 끝날 때 까지 살아있음, 브라우저당 1개
//@RequestScope => 요청 당 객체가 1개 요청이 끝날 때 까지 살아 있음,  수명이 더 짧음
//@PrototypeScope= > 그냥 매번 새로 만듬
@Configuration
@RequiredArgsConstructor
public class RebateOrderItemConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final OrderItemRepository orderItemRepository; //읽을 대상
    private final RebateOrderItemRepository rebateOrderItemRepository; //쓸 대상

    @Bean
    public Job makeRebateOrderItemJob(Step makeRebateOrderItemStep1, CommandLineRunner initData) throws Exception{
        initData.run();

        return jobBuilderFactory.get("makeRebateOrderItemJob")
                //.incrementer(new RunIdIncrementer()) //강제로 매번 다른 ID를 실행시에 파라미터로 부여
                .start(makeRebateOrderItemStep1)
                .build();
    }

    @Bean
    @JobScope
    public Step makeRebateOrderItemStep1(
            ItemReader orderItemReader,
            ItemProcessor orderItemToRebateOrderItemProcessor,
            ItemWriter rebateOrderItemWriter
    ) {
        return stepBuilderFactory.get("makeRebateOrderItemStep1")
                .<OrderItem, RebateOrderItem>chunk(100) //입력과 출력, 한번에 받아오는 값
                .reader(orderItemReader)
                .processor(orderItemToRebateOrderItemProcessor)
                .writer(rebateOrderItemWriter)
                .build();
    }


    @StepScope
    @Bean
    public RepositoryItemReader<OrderItem> orderItemReader(
            @Value("#{jobParameters['month']}") String yearMonth
    ) {
        int monthEndDay = Ut.date.getEndDayOf(yearMonth);
        LocalDateTime fromDate = Ut.date.parse(yearMonth + "-01 00:00:00.000000");
        LocalDateTime toDate = Ut.date.parse(yearMonth + "-%02d 23:59:59.999999".formatted(monthEndDay));

        return new RepositoryItemReaderBuilder<OrderItem>()
                .name("orderItemReader")
                .repository(orderItemRepository)
                .methodName("findAllByPayDateBetween")
                .pageSize(100)
                .arguments(Arrays.asList(fromDate, toDate))
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build();
    }

    @StepScope
    @Bean
    public ItemProcessor<OrderItem, RebateOrderItem> orderItemToRebateOrderItemProcessor() {
        return orderItem -> new RebateOrderItem(orderItem);
    }

    @StepScope
    @Bean
    public ItemWriter<RebateOrderItem> rebateOrderItemWriter() {
        return items -> items.forEach(item -> {
            RebateOrderItem oldRebateOrderItem = rebateOrderItemRepository.findByOrderItemId(item.getOrderItem().getId()).orElse(null);

            if (oldRebateOrderItem != null) {
                rebateOrderItemRepository.delete(oldRebateOrderItem);
            }

            rebateOrderItemRepository.save(item);
        });
    }
}
