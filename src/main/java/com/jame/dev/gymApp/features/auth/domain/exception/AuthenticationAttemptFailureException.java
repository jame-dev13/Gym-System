package com.jame.dev.gymApp.features.auth.domain.exception;

public class AuthenticationAttemptFailureException extends RuntimeException {
   public AuthenticationAttemptFailureException(String message) {
      super(message);
   }
}
