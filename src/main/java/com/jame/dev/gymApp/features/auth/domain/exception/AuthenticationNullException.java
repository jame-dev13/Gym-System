package com.jame.dev.gymApp.features.auth.domain.exception;

public class AuthenticationNullException extends RuntimeException {
   public AuthenticationNullException(String message) {
      super(message);
   }
}
