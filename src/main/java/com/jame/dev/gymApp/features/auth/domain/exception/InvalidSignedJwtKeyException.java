package com.jame.dev.gymApp.features.auth.domain.exception;

public class InvalidSignedJwtKeyException extends RuntimeException {
   public InvalidSignedJwtKeyException(String message, Throwable e) {
      super(message, e);
   }
}
