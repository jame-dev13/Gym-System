package com.jame.dev.gymApp.features.auth.infrastructure.aspect;

import com.jame.dev.gymApp.application.contract.TokenGeneratorService;
import com.jame.dev.gymApp.domain.exception.EventPublisherException;
import com.jame.dev.gymApp.features.auth.api.request.RecoveryRequest;
import com.jame.dev.gymApp.features.auth.domain.event.RecoverySenderEvent;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class PublishRecoveryAspect {
   private final ApplicationEventPublisher applicationEventPublisher;
   private final TokenGeneratorService tokenGeneratorService;

   @AfterReturning(
      value = "@annotation(com.jame.dev.gymApp.features.auth.infrastructure.annotation.PublishRecovery) && args(recoveryRequest)",
      returning = "response"
   )
   public void publishRecovery(final RecoveryRequest recoveryRequest, ResponseEntity<Void> response) {
      if (!response.getStatusCode().is2xxSuccessful()) {
         throw new EventPublisherException("Cannot send recovery email right now.");
      }
      applicationEventPublisher.publishEvent(new RecoverySenderEvent(
         recoveryRequest.email(),
         tokenGeneratorService.generateToken())
      );
   }

}
