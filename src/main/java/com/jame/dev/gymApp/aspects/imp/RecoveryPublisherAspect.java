package com.jame.dev.gymApp.aspects.imp;

import com.jame.dev.gymApp.model.dto.in.RecoveryRequest;
import com.jame.dev.gymApp.model.listeners.RecoverySender;
import com.jame.dev.gymApp.service.in.TokenGeneratorService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class RecoveryPublisherAspect {
   private final ApplicationEventPublisher applicationEventPublisher;
   private final TokenGeneratorService tokenGeneratorService;

   @Before("@annotation(com.jame.dev.gymApp.aspects.annotations.PublishRecovery) && args(recoveryRequest)")
   public void publishRecovery(final RecoveryRequest recoveryRequest) {
      final RecoverySender recoverySender = new RecoverySender(
              recoveryRequest.email(), tokenGeneratorService.generateToken()
      );

      applicationEventPublisher.publishEvent(recoverySender);
   }

}
