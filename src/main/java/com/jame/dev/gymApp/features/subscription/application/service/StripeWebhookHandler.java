package com.jame.dev.gymApp.features.subscription.application.service;

import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.CompletedCheckoutUseCase;
import com.jame.dev.gymApp.features.subscription.domain.event.CompletedCheckoutEvent;
import com.jame.dev.gymApp.features.subscription.domain.exception.PayloadException;
import com.jame.dev.gymApp.features.subscription.domain.exception.SessionSignatureVerificationException;
import com.jame.dev.gymApp.features.subscription.domain.repository.PaymentValidationRepository;
import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
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

        if (!"checkout.session.completed".equals(event.getType())) {
            return ResponseEntity.ok().build();
        }

        final EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
        final StripeObject stripeObject;
        try {
            stripeObject = deserializer.deserializeUnsafe();
        } catch (EventDataObjectDeserializationException e) {
            log.warn("Failed to deserialize event data: {}", e.getMessage());
            throw new PayloadException("Failed to deserialize session data");
        }

        if (!(stripeObject instanceof final Session session)) {
            log.warn("Unexpected payload in Stripe webhook");
            throw new PayloadException("session payload unexpected.");
        }

        final String pricingIdStr = session.getMetadata().get("pricingId");
        final String customerEmail = session.getMetadata().get("customerEmail");

        if (pricingIdStr == null || customerEmail == null) {
            log.warn("Missing metadata in Stripe session: {}", session.getId());
            throw new PayloadException("Missing metadata.");
        }

        if (paymentValidationRepository.existsBySessionId(session.getId())) {
            log.info("Payment already processed for session: {}", session.getId());
            return ResponseEntity.ok().build();
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
