package com.jame.dev.gymApp.infrastructure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.scheduler.config")
public record SchedulerProperties(
   String threadNamePrefix,
   Integer poolSize,
   Integer awaitTerminationSeconds
) {
   public SchedulerProperties {
      threadNamePrefix = threadNamePrefix == null ? "scheduler-app-thread" : threadNamePrefix;
      poolSize = poolSize == null ? 10 : poolSize;
      awaitTerminationSeconds = awaitTerminationSeconds == null ? 30 : awaitTerminationSeconds;
   }
}
