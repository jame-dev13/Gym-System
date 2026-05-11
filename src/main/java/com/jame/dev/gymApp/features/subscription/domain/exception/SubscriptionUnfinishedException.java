package com.jame.dev.gymApp.features.subscription.domain.exception;

public class SubscriptionUnfinishedException extends RuntimeException {
   public SubscriptionUnfinishedException(String message) {
      super(message);
   }
}
