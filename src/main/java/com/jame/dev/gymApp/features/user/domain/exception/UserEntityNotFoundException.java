package com.jame.dev.gymApp.features.user.domain.exception;

public class UserEntityNotFoundException extends RuntimeException {
   public UserEntityNotFoundException(String message) {
      super(message);
   }
}
