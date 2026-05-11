package com.jame.dev.gymApp.features.auth.domain.exception;

public class AlreadyExistsException extends RuntimeException {
   public AlreadyExistsException(String message) {
      super(message);
   }
}
