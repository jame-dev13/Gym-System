package com.jame.dev.gymApp.features.auth.domain.exception;

public class IllegalSubjectAuthenticatedException extends RuntimeException {
   public IllegalSubjectAuthenticatedException(String message) {
      super(message);
   }
}
