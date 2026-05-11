package com.jame.dev.gymApp.domain.exception;

public class EmailNotFoundException extends RuntimeException {
   public EmailNotFoundException(String message) {
      super(message);
   }
}
