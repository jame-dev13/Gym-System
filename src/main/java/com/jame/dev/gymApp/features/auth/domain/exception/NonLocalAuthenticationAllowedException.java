package com.jame.dev.gymApp.features.auth.domain.exception;

public class NonLocalAuthenticationAllowedException extends RuntimeException {
   public NonLocalAuthenticationAllowedException(String message) {
      super(message);
   }
}
