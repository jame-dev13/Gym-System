package com.jame.dev.gymApp.features.subscription.domain.exception;

public class StripeSessionException extends RuntimeException {
   public StripeSessionException(String message) {
      super(message);
   }

   public StripeSessionException(String message, Throwable cause) {
        super(message, cause);
    }
}
