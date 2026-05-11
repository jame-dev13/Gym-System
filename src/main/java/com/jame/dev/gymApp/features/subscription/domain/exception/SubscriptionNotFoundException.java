package com.jame.dev.gymApp.features.subscription.domain.exception;

public class SubscriptionNotFoundException extends RuntimeException {
   public SubscriptionNotFoundException(String message) {
      super(message);
   }
}
