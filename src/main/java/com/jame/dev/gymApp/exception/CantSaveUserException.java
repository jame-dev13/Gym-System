package com.jame.dev.gymApp.exception;

public class CantSaveUserException extends RuntimeException {
   public CantSaveUserException(String message) {
      super(message);
   }
}
