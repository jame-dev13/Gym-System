package com.jame.dev.gymApp.infrastructure.config.app;


import com.jame.dev.gymApp.infrastructure.properties.SchedulerProperties;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.time.Clock;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SchedulerConfig implements SchedulingConfigurer {

   private final SchedulerProperties schedulerProperties;
   private final Clock clock;

   @Override
   public void configureTasks(@NonNull ScheduledTaskRegistrar taskRegistrar) {
      ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();

      scheduler.setClock(clock);
      scheduler.setPoolSize(schedulerProperties.poolSize());
      scheduler.setThreadNamePrefix(schedulerProperties.threadNamePrefix());
      scheduler.setWaitForTasksToCompleteOnShutdown(true);
      scheduler.setAwaitTerminationSeconds(schedulerProperties.awaitTerminationSeconds());
      scheduler.setErrorHandler(ex ->
         log.error("Error executing scheduled task", ex)
      );

      scheduler.initialize();

      taskRegistrar.setTaskScheduler(scheduler);
   }
}
