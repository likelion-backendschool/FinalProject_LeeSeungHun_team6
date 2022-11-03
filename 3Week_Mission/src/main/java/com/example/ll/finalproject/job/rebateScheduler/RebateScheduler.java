package com.example.ll.finalproject.job.rebateScheduler;

import com.example.ll.finalproject.job.rebateOrderItem.RebateOrderItemJobConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RebateScheduler {
    private final JobLauncher jobLauncher;
    private final RebateOrderItemJobConfig rebateOrderItemJobConfig;

    //확인용으로 매 3초마다 실행
    //매달 15일 새벽4시 정산
    @Scheduled(cron="0 0 4 15 * *")
//    @Scheduled(cron = "10 * * * * *")
    public void runJob() {
        // job parameter 설정
        Map<String, JobParameter> confMap = new HashMap<>();
        confMap.put("time", new JobParameter(System.currentTimeMillis()));
        JobParameters jobParameters = new JobParameters(confMap);

        try {
            jobLauncher.run(rebateOrderItemJobConfig.makeRebateOrderItemJob(), jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobInstanceAlreadyCompleteException
                 | JobParametersInvalidException | org.springframework.batch.core.repository.JobRestartException e) {

            System.out.println(e.getMessage());
        }
    }
}
