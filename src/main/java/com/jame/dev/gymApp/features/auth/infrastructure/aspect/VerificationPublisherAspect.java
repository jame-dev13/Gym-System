package com.jame.dev.gymApp.features.auth.infrastructure.aspect;

import com.jame.dev.gymApp.features.auth.api.request.RegisterRequest;
import com.jame.dev.gymApp.features.user.domain.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class VerificationPublisherAspect {
   private final ApplicationEventPublisher applicationEventPublisher;

   @AfterReturning(
      "@annotation(com.jame.dev.gymApp.features.auth.infrastructure.annotation.PublishVerify) && args(register)"
   )
   public void publishVerification(final RegisterRequest register) {
      System.out.println("Before signUp");
      applicationEventPublisher.publishEvent(new UserRegisteredEvent(register.email()));
   }
}
