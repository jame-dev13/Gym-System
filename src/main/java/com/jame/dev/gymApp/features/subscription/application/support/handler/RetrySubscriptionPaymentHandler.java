package com.jame.dev.gymApp.features.subscription.application.support.handler;

import com.jame.dev.gymApp.domain.exception.EntityNotFoundException;
import com.jame.dev.gymApp.features.subscription.api.response.RetryResponse;
import com.jame.dev.gymApp.features.subscription.application.support.resolver.PaymentSessionStatusResolver;
import com.jame.dev.gymApp.features.subscription.domain.model.PaymentEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.PaymentStatus;
import com.jame.dev.gymApp.features.subscription.domain.repository.PaymentQueryRepository;
import com.jame.dev.gymApp.features.subscription.infrastructure.stripe.service.StripeCheckoutGateway;
import com.jame.dev.gymApp.infrastructure.async.AsyncResolver;
import com.stripe.model.checkout.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RetrySubscriptionPaymentHandler {

   private final PaymentSessionStatusResolver paymentSessionStatusResolver;
   private final PaymentQueryRepository paymentQueryRepository;
   private final StripeCheckoutGateway stripeCheckoutGateway;

   public ResponseEntity<RetryResponse> handleSubscriptionPaymentRetry(final String subject) {

      final String sessionId = paymentQueryRepository.findByCustomerEmailAndStatus(subject, PaymentStatus.PENDING)
         .map(PaymentEntity::getStripeSessionId)
         .orElseThrow(() -> new EntityNotFoundException("Payment not found for customer: " + subject));

      final Session session = AsyncResolver.getResult(
         () -> stripeCheckoutGateway.retrieveSession(sessionId), 20
      );

      log.info("Session located: {}", session.getId());

      return paymentSessionStatusResolver.resolvePaymentStatus(session, subject);
   }
}
