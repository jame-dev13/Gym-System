package com.jame.dev.gymApp.features.auth.domain.exception;

public class AuthenticationNullException extends RuntimeException {
   private static final String MSG = "Authentication undefined.";
   public AuthenticationNullException(String message) {
      super(message);
   }

   public AuthenticationNullException() {
      super(MSG);
   }
}
