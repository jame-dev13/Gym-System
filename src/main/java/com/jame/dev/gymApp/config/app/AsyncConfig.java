package com.jame.dev.gymApp.config.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@EnableAsync
@Configuration
public class AsyncConfig {

   @Bean("taskExecutor")
   public Executor taskExecutor() {
      return Executors.newVirtualThreadPerTaskExecutor();
   }
}
