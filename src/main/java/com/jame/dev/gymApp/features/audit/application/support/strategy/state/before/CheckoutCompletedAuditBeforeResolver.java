package com.jame.dev.gymApp.features.audit.application.support.strategy.state.before;

import com.jame.dev.gymApp.domain.exception.NotFoundException;
import com.jame.dev.gymApp.features.audit.application.model.AuditExecutionContext;
import com.jame.dev.gymApp.features.audit.application.model.BeforeCompletedCheckoutModel;
import com.jame.dev.gymApp.features.audit.domain.exception.AuditResolverException;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.infrastructure.audit_strategy.AuditBeforeResolver;
import com.jame.dev.gymApp.features.audit.infrastructure.spel_evaluator.AuditLogExpressionEvaluator;
import com.jame.dev.gymApp.features.subscription.domain.event.CompletedCheckoutEvent;
import com.jame.dev.gymApp.features.subscription.domain.repository.PaymentQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CheckoutCompletedAuditBeforeResolver implements AuditBeforeResolver {
   private final AuditLogExpressionEvaluator evaluator;
   private final PaymentQueryRepository paymentQueryRepository;

   @Override
   public AuditLogAction action() {
      return AuditLogAction.CHECKOUT;
   }

   @Override
   public void resolve(AuditExecutionContext context) {
      final var input = evaluator.evaluateAsObject(context.getAnnotation().input(), context.getParamNames(), context.getArgs());

      if (!(input instanceof CompletedCheckoutEvent event)) {
         throw new AuditResolverException("Something went wrong while resolving audit operation.");
      }

      final var payment = paymentQueryRepository.findByStripeSessionId(event.stripeSessionId())
         .orElseThrow(() -> new NotFoundException("Payment not found."));

      context.setInput(
         BeforeCompletedCheckoutModel.builder()
            .paymentStatus(payment.getStatus())
            .customerEmail(event.customerEmail())
            .amount(payment.getAmount())
            .build()
      );
   }
}
