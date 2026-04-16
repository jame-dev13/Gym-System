package com.jame.dev.gymApp.aspects.imp.aspects;

import com.jame.dev.gymApp.model.listeners.CustomerDeletedEvent;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class PublishCustomerDeleted {
   private final ApplicationEventPublisher applicationEventPublisher;

   @Before("@annotation(com.jame.dev.gymApp.aspects.annotations.aspects.PublishCustomerDeleted) && args(id)")
   public void publishDeleteSubscriptionEvent(long id) {
      applicationEventPublisher.publishEvent(new CustomerDeletedEvent(id));
   }
}
