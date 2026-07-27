package com.jame.dev.gymApp.domain.exception;

public class NotFoundException extends RuntimeException {
   public NotFoundException(String message) {
      super(message);
   }
}
