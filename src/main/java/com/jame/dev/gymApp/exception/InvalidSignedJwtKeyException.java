package com.jame.dev.gymApp.exception;

public class InvalidSignedJwtKeyException extends RuntimeException {
   public InvalidSignedJwtKeyException(String message, Throwable e) {
      super(message, e);
   }
   public InvalidSignedJwtKeyException(String message) {
      super(message);
   }
}
