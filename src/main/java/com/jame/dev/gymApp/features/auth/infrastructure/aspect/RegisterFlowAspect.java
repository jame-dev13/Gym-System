package com.jame.dev.gymApp.features.auth.infrastructure.aspect;

import com.jame.dev.gymApp.features.auth.api.request.RegisterRequest;
import com.jame.dev.gymApp.features.auth.infrastructure.validation.RegisterValidationRules;
import com.jame.dev.gymApp.features.user.domain.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RegisterFlowAspect {
   private final RegisterValidationRules validationRules;
   private final ApplicationEventPublisher publisher;

   @Around(
      value = "@annotation(com.jame.dev.gymApp.features.auth.infrastructure.annotation.RegisterFlow) && args(registerRequest)",
      argNames = "jp,registerRequest"
   )
   public Object registerFlow(
      final ProceedingJoinPoint jp,
      final RegisterRequest registerRequest
   ) throws Throwable {

      final String email = registerRequest.email();

      validationRules.validateBeforeExecuteService(email);

      try {
         final Object result = jp.proceed();
         if (!(result instanceof Boolean completed))
            throw new IllegalStateException("Something went wrong.");
         if (completed) {
            publisher.publishEvent(new UserRegisteredEvent(email));
            log.info("User Registered event published.");
         }
         return completed;
      } catch (Throwable th) {
         log.error("Error on {} while executing service register method.", getClass().getSimpleName(), th);
         throw th;
      }
   }
}
