package com.jame.dev.gymApp.aspects.imp.aspects;

import com.jame.dev.gymApp.model.listeners.UserRecoveredEvent;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class PublishUserRecoveredAspect {
   private final ApplicationEventPublisher applicationEventPublisher;

   @AfterReturning("@annotation(com.jame.dev.gymApp.aspects.annotations.aspects.PublishUserRecovered) && args(id)")
   public void publishUserRecovered(long id) {
      applicationEventPublisher.publishEvent(new UserRecoveredEvent(id));
   }
}
