package com.jame.dev.gymApp.features.user.domain.exception;

public class CantSaveUserException extends RuntimeException {
   public CantSaveUserException(String message) {
      super(message);
   }
}
