package com.jame.dev.gymApp.exception;

public class InvalidJwtSecretException extends RuntimeException {
   public InvalidJwtSecretException(String message, Throwable e) {
      super(message, e);
   }
}
