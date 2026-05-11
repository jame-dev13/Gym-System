package com.jame.dev.gymApp.features.auth.domain.exception;

public class AlreadyVerifiedException extends RuntimeException {
   public AlreadyVerifiedException(String message) {
      super(message);
   }
}
