package com.jame.dev.gymApp.exception;

public class EmailNotFoundException extends RuntimeException {
   public EmailNotFoundException(String message) {
      super(message);
   }
}
