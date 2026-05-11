package com.jame.dev.gymApp.features.auth.domain.exception;

public class VerificationTokenNotFoundException extends RuntimeException {
   public VerificationTokenNotFoundException(String message) {
      super(message);
   }
}
