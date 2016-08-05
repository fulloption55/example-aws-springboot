package com.example.scheduler;

import com.example.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class JobScheduler {

  @Autowired
  BusinessService businessService;

  @Scheduled(cron = "${cron.expression}")
  public void onTimeMonitoringReconciliationFile() {
    businessService.doCronJob();
  }

}
