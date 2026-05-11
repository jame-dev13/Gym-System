package com.jame.dev.gymApp.features.auth.domain.exception;

public class InvalidJwtException extends RuntimeException {
   public InvalidJwtException(String message) {
      super(message);
   }
}
