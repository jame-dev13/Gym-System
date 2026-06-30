package com.jame.dev.gymApp.features.subscription.application.support.factory;

import com.jame.dev.gymApp.features.subscription.domain.event.StripeSessionPaymentEvent;
import com.jame.dev.gymApp.features.subscription.domain.model.PaymentEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.PaymentMethod;
import org.springframework.stereotype.Component;

@Component
public class PaymentFactory {
   public PaymentEntity from(StripeSessionPaymentEvent paymentEvent) {
      return PaymentEntity.builder()
         .stripeSessionId(paymentEvent.sessionId())
         .stripePaymentIntentId(paymentEvent.intentId())
         .stripeSubscriptionId(paymentEvent.subscriptionId())
         .amount(paymentEvent.subscriptionEntity().getPricing().getPrice())
         .currency("mx")
         .status(paymentEvent.paymentStatus())
         .paymentMethod(paymentEvent.isPhysicSession() ? PaymentMethod.PHYSIC : PaymentMethod.ELECTRONIC)
         .subscription(paymentEvent.subscriptionEntity())
         .customer(paymentEvent.subscriptionEntity().getCustomer())
         .build();
   }
}
