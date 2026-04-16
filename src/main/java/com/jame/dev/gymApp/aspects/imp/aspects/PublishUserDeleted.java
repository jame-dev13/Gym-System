package com.jame.dev.gymApp.aspects.imp.aspects;

import com.jame.dev.gymApp.model.listeners.UserDeletedEvent;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class PublishUserDeleted {
   private final ApplicationEventPublisher applicationEventPublisher;

   @Before("@annotation(com.jame.dev.gymApp.aspects.annotations.aspects.PublishUserDeleted) && args(id)")
   public void publishUserDeleteEvent(long id) {
      applicationEventPublisher.publishEvent(new UserDeletedEvent(id));
   }
}
