package com.jame.dev.gymApp.domain.exception;

public class NoActiveException extends RuntimeException {
   public NoActiveException(String message) {
      super(message);
   }
}
