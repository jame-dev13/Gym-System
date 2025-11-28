package com.jame.dev.gymApp.exception;

public class AuthenticationAttemptFailureException extends RuntimeException {
   public AuthenticationAttemptFailureException(String message) {
      super(message);
   }
}
