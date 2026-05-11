package com.jame.dev.gymApp.features.auth.domain.exception;

public class TooManyRequestsException extends RuntimeException {
   public TooManyRequestsException(String message) {
      super(message);
   }
}
