package com.jame.dev.gymApp.features.subscription.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentPhysicMeta {
   SESSION_ID("SESSION_LOCAL_ID"),
   PAYMENT_INTEND_ID("PAYMENT_LOCAL_ID"),
   STRIPE_SUBSCRIPTION_ID("SUBSCRIPTION_LOCAL_ID"),
   ;

   private final String value;
}
