package com.jame.dev.gymApp.features.auth.domain.exception;

public class VerificationNotFoundException extends RuntimeException {
   public VerificationNotFoundException(String msg) {
      super(msg);
   }
}
