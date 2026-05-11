package com.jame.dev.gymApp.features.auth.domain.exception;

public class TemporaryBlockedException extends RuntimeException {
   public TemporaryBlockedException(String message) {
      super(message);
   }
}
