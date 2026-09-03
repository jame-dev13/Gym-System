package com.jame.dev.gymApp.infrastructure.config.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@EnableAsync
@Configuration
public class AsyncConfig {

   @Bean("taskExecutor")
   public Executor taskExecutor() {
      return Executors.newVirtualThreadPerTaskExecutor();
   }

   @Bean("mailExecutor")
   public Executor mailExecutor() {
      final ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
      taskExecutor.setCorePoolSize(4);
      taskExecutor.setMaxPoolSize(10);
      taskExecutor.setQueueCapacity(100);
      taskExecutor.setThreadNamePrefix("mail-");
      taskExecutor.initialize();

      return taskExecutor;
   }

}
