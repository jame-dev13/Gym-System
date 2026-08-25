package com.jame.dev.gymApp.features.notification.infrastructure.aspect;

import com.jame.dev.gymApp.features.auth.domain.model.AuthPrincipal;
import com.jame.dev.gymApp.features.notification.domain.exception.SubscriberException;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionStatus;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionValidationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class CheckSubscriberAspect {

   private final SubscriptionValidationRepository subscriptionValidationRepository;


   @Around(
      value = """
         @annotation(com.jame.dev.gymApp.features.notification.infrastructure.annotation.CheckSubscriber)
         && args(principal)
         """,
      argNames = "jp,principal"
   )
   public Object checkSubscriber(final ProceedingJoinPoint jp, final AuthPrincipal principal) throws Throwable {
      final String username = principal.username();
      if (!subscriptionValidationRepository.existsByCustomerEmail(principal.username())) {
         throw new SubscriberException(username + " is not a subscriber.");
      }

      if (!subscriptionValidationRepository.existsByCustomerEmailAndStatus(username, SubscriptionStatus.PAID)) {
         throw new SubscriberException(username + " doesn't have a valid subscription.");
      }

      try {
         return jp.proceed();
      } catch (Throwable ex) {
         log.error("Something went wrong: {}", ex.getMessage(), ex);
         throw ex;
      }
   }
}
