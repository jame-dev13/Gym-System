package com.jame.dev.gymApp.features.user.domain.exception;

public class UserNotVerifiedException extends RuntimeException {
   public UserNotVerifiedException(String message) {
      super(message);
   }
}
