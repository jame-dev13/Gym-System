package com.jame.dev.gymApp.aspects.imp.aspects;

import com.jame.dev.gymApp.model.dto.in.RecoveryRequest;
import com.jame.dev.gymApp.model.listeners.PasswordResetEvent;
import com.jame.dev.gymApp.service.in.TokenGeneratorService;
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

   @Before("@annotation(com.jame.dev.gymApp.aspects.annotations.aspects.PublishPasswordResetRequest) && args(recoveryRequest)")
   public void publishPasswordResetRequest(final RecoveryRequest recoveryRequest) {
      applicationEventPublisher.publishEvent(
         new PasswordResetEvent(
            recoveryRequest.email(),
            tokenGeneratorService.generateTokenOneTimeToken()
         )
      );
   }
}
