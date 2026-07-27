package com.jame.dev.gymApp.domain.exception;

public class LockException extends RuntimeException {
   public LockException(String message) {
      super(message);
   }

   public LockException(String message, Throwable cause) {
      super(message, cause);
   }
}
