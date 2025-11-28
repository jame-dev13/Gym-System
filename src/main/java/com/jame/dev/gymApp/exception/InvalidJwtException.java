package com.jame.dev.gymApp.exception;

public class InvalidJwtException extends RuntimeException {
   public InvalidJwtException(String message) {
      super(message);
   }
}
