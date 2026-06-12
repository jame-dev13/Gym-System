package com.jame.dev.gymApp.features.user.infrastructure.aspect;

import com.jame.dev.gymApp.features.user.domain.event.UserRecoveredEvent;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class UserRecoveredAspectPublisher {
   private final ApplicationEventPublisher applicationEventPublisher;

   @AfterReturning("@annotation(com.jame.dev.gymApp.features.user.infrastructure.annotations.PublishUserRecovered) && args(id)")
   public void publishUserRecovered(long id) {
      applicationEventPublisher.publishEvent(new UserRecoveredEvent(id));
   }
}
