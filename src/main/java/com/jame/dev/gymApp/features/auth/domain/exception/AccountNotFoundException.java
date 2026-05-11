package com.jame.dev.gymApp.features.auth.domain.exception;

public class AccountNotFoundException extends RuntimeException {
   public AccountNotFoundException(String message) {
      super(message);
   }
}
