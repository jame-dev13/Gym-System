package com.jame.dev.gymApp.features.auth.infrastructure.aspect;

import com.jame.dev.gymApp.features.auth.api.request.RegisterRequest;
import com.jame.dev.gymApp.features.user.domain.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Deprecated
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class VerificationPublisherAspect {
   private final ApplicationEventPublisher applicationEventPublisher;

   @AfterReturning(
      "@annotation(com.jame.dev.gymApp.features.auth.infrastructure.annotation.PublishVerify) && args(register)"
   )
   public void publishVerification(final RegisterRequest register) {
      applicationEventPublisher.publishEvent(new UserRegisteredEvent(register.email()));
      log.info("Published verification event for email: {}", register.email());
   }
}
