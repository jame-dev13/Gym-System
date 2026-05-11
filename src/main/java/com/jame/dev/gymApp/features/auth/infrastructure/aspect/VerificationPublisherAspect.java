package com.jame.dev.gymApp.features.auth.infrastructure.aspect;

import com.jame.dev.gymApp.features.user.api.request.UserRequest;
import com.jame.dev.gymApp.features.user.domain.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class VerificationPublisherAspect {
   private final ApplicationEventPublisher applicationEventPublisher;

   @AfterReturning(
           pointcut = "@annotation(com.jame.dev.gymApp.app.auth.infrastructure.annotation.PublishVerify) && args(user, ..)",
           returning = "response"
   )
   public void publishVerification(final UserRequest user, final ResponseEntity<Void> response) {
      if (response != null && response.getStatusCode().is2xxSuccessful()) {
         applicationEventPublisher.publishEvent(new UserRegisteredEvent(user.email()));
      }
   }
}
