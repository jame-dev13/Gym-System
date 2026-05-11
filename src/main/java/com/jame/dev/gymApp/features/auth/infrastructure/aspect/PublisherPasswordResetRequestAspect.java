package com.jame.dev.gymApp.features.auth.infrastructure.aspect;

import com.jame.dev.gymApp.features.auth.api.request.RecoveryRequest;
import com.jame.dev.gymApp.features.auth.domain.event.PasswordResetEvent;
import com.jame.dev.gymApp.application.contract.TokenGeneratorService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class PublisherPasswordResetRequestAspect {
   private final ApplicationEventPublisher applicationEventPublisher;
   private final TokenGeneratorService tokenGeneratorService;

   @Before("@annotation(com.jame.dev.gymApp.app.auth.infrastructure.annotation.PublishPasswordResetRequest) && args(recoveryRequest)")
   public void publishPasswordResetRequest(final RecoveryRequest recoveryRequest) {
      applicationEventPublisher.publishEvent(
         new PasswordResetEvent(
            recoveryRequest.email(),
            tokenGeneratorService.generateTokenOneTimeToken()
         )
      );
   }
}
