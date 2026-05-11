package com.jame.dev.gymApp.features.auth.domain.exception;

public class ExpiredException extends RuntimeException {
   public ExpiredException(String message) {
      super(message);
   }
}
