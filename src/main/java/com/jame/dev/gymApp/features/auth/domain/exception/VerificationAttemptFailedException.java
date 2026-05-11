package com.jame.dev.gymApp.features.auth.domain.exception;

public class VerificationAttemptFailedException extends RuntimeException {
   public VerificationAttemptFailedException(String message) {
      super(message);
   }
}
