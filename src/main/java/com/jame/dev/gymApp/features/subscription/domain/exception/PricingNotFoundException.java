package com.jame.dev.gymApp.features.subscription.domain.exception;

public class PricingNotFoundException extends RuntimeException {
   public PricingNotFoundException(String message) {
      super(message);
   }
}
