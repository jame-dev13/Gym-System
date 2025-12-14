package com.jame.dev.gymApp.exception;

public class NonLocalAuthenticationAllowedException extends RuntimeException {
   public NonLocalAuthenticationAllowedException(String message) {
      super(message);
   }
}
