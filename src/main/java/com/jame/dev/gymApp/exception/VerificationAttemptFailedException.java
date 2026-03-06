package com.jame.dev.gymApp.exception;

public class VerificationAttemptFailedException extends RuntimeException {
   public VerificationAttemptFailedException(String message) {
      super(message);
   }
}
