package com.jame.dev.gymApp.exception;

public class AccountNotFoundException extends RuntimeException {
   public AccountNotFoundException(String message) {
      super(message);
   }
}
