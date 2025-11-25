package com.jame.dev.gymApp.exception;

public class CustomerNotFoundException extends RuntimeException {
   public CustomerNotFoundException(String message) {
      super(message);
   }
}
