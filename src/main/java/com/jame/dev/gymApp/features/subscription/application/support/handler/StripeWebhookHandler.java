package com.jame.dev.gymApp.features.subscription.application.support.handler;

import com.jame.dev.gymApp.features.auth.domain.exception.AlreadyExistsException;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.CompletedCheckoutUseCase;
import com.jame.dev.gymApp.features.subscription.domain.event.CompletedCheckoutEvent;
import com.jame.dev.gymApp.features.subscription.domain.exception.PayloadException;
import com.jame.dev.gymApp.features.subscription.domain.exception.SessionSignatureVerificationException;
import com.jame.dev.gymApp.features.subscription.domain.model.PaymentStatus;
import com.jame.dev.gymApp.features.subscription.domain.repository.PaymentValidationRepository;
import com.jame.dev.gymApp.features.subscription.infrastructure.stripe.handler.EventSessionDeserializerHandler;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripeWebhookHandler {

   @Value("${stripe.webhook-secret}")
   private String webhookSecret;

   private final PaymentValidationRepository paymentValidationRepository;
   private final CompletedCheckoutUseCase completedCheckoutUseCase;

   public ResponseEntity<Void> handle(String payload, String signatureHeader) {
      final Event event;
      try {
         event = Webhook.constructEvent(payload, signatureHeader, webhookSecret);
      } catch (SignatureVerificationException e) {
         log.warn("Invalid Stripe webhook signature: {}", e.getMessage());
         throw new SessionSignatureVerificationException("Cannot proceed with the payment.");
      }

      if(!"checkout.session.completed".equals(event.getType())){
         log.info("[HIT]: Event Type: {}", event.getType());
         return ResponseEntity.ok().build();
      }

      final Session session = EventSessionDeserializerHandler.handleDeserializeEvent(event);

      final String customerEmail = session.getMetadata().get("customerEmail");
      final String pricingIdStr = session.getMetadata().get("pricingId");

      if (customerEmail == null || pricingIdStr == null) {
         throw new PayloadException("Values not present in the payload.");
      }

      if (paymentValidationRepository.existsBySessionIdAndPaymentStatus(session.getId(), PaymentStatus.COMPLETED)) {
         throw new AlreadyExistsException("Payment already processed.");
      }

      completedCheckoutUseCase.execute(new CompletedCheckoutEvent(
         session.getId(),
         session.getPaymentIntent(),
         session.getSubscription(),
         customerEmail
      ));

      return ResponseEntity.ok().build();
   }
}
