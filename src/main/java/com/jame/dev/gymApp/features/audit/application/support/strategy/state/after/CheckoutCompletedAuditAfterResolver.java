package com.jame.dev.gymApp.features.audit.application.support.strategy.state.after;

import com.jame.dev.gymApp.features.audit.application.model.AfterCompletedCheckoutModel;
import com.jame.dev.gymApp.features.audit.application.model.AuditExecutionContext;
import com.jame.dev.gymApp.features.audit.domain.exception.AuditResolverException;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.infrastructure.audit_strategy.AuditAfterResolver;
import com.jame.dev.gymApp.features.audit.infrastructure.spel_evaluator.AuditLogExpressionEvaluator;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.features.subscription.application.dto.CompletedCheckoutResult;
import com.jame.dev.gymApp.features.subscription.domain.model.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CheckoutCompletedAuditAfterResolver implements AuditAfterResolver {
   private final AuditLogExpressionEvaluator evaluator;

   @Override
   public AuditLogAction action() {
      return AuditLogAction.CHECKOUT;
   }

   @Override
   public void resolve(AuditExecutionContext context) {
      final var result = evaluator.evaluateAsObject(context.getAnnotation().result(), context.getResult());

      if (!(result instanceof CompletedCheckoutResult(
         PaymentStatus paymentStatus, SubscriptionResponse subscription
      ))) {
         throw new AuditResolverException("Cannot resolve after audit operation.");
      }

      context.setResultValue(
         AfterCompletedCheckoutModel.builder()
            .paymentStatus(paymentStatus)
            .customerEmail(subscription.customerEmail())
            .amount(subscription.price())
            .membership(subscription.membership())
            .subscriptionStatus(subscription.status())
            .build()
      );

      context.setEntityId(subscription.id());
   }
}
