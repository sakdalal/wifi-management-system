package com.sak.wifi.scheduler;


import com.sak.wifi.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "scheduler.enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class BillingScheduler {

    private final BillService billService;

    @Scheduled(cron="0 0 0 1 * *")
    public void generateBills(){
        System.out.println("Scheduler triggered...");
        billService.generateMonthlyBills();
    }
}
