package com.jame.dev.gymApp.aspects.imp.aspects;

import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class VerificationPublisherAspect {
   private final ApplicationEventPublisher applicationEventPublisher;

   @AfterReturning(
           pointcut = "@annotation(com.jame.dev.gymApp.aspects.annotations.aspects.PublishVerify) && args(user, ..)",
           returning = "response"
   )
   public void publishVerification(final UserDtoInput user, final ResponseEntity<Void> response) {
      if (response != null && response.getStatusCode().is2xxSuccessful()) {
         applicationEventPublisher.publishEvent(user.email());
      }
   }
}
