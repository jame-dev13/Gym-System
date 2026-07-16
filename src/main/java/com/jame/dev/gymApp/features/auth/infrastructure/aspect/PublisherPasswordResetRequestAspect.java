package com.jame.dev.gymApp.features.auth.infrastructure.aspect;

import com.jame.dev.gymApp.infrastructure.security.token.TokenGeneratorService;
import com.jame.dev.gymApp.domain.exception.EventPublisherException;
import com.jame.dev.gymApp.features.auth.api.request.RecoveryRequest;
import com.jame.dev.gymApp.features.auth.domain.event.PasswordResetEvent;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class PublisherPasswordResetRequestAspect {
   private final ApplicationEventPublisher applicationEventPublisher;
   private final TokenGeneratorService tokenGeneratorService;

   @AfterReturning(
      value = "@annotation(com.jame.dev.gymApp.features.auth.infrastructure.annotation.PublishPasswordResetRequest) && args(recoveryRequest)",
      returning = "response"
   )
   public void publishPasswordResetRequest(final RecoveryRequest recoveryRequest, final ResponseEntity<Void> response) {
      if (!response.getStatusCode().is2xxSuccessful()) {
         throw new EventPublisherException("Cannot send recovery link for now.");
      }
      applicationEventPublisher.publishEvent(
         new PasswordResetEvent(
            recoveryRequest.email(),
            tokenGeneratorService.generateTokenOneTimeToken()
         )
      );
   }
}
