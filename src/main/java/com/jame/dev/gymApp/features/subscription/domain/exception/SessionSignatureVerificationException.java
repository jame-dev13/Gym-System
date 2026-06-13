package com.jame.dev.gymApp.features.subscription.domain.exception;

public class SessionSignatureVerificationException extends RuntimeException {
   public SessionSignatureVerificationException(String message) {
      super(message);
   }
}
