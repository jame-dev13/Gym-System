package com.jame.dev.gymApp.aspects.imp.aspects;

import com.jame.dev.gymApp.model.dto.in.RecoveryRequest;
import com.jame.dev.gymApp.model.listeners.RecoverySenderEvent;
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

   @Before("@annotation(com.jame.dev.gymApp.aspects.annotations.aspects.PublishRecovery) && args(recoveryRequest)")
   public void publishRecovery(final RecoveryRequest recoveryRequest) {
      applicationEventPublisher.publishEvent(new RecoverySenderEvent(
              recoveryRequest.email(),
              tokenGeneratorService.generateToken())
      );
   }

}
