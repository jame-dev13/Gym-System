package com.jame.dev.gymApp.domain.exception;

public class UnrelatedDataAccessException extends RuntimeException {
   public UnrelatedDataAccessException(String message) {
      super(message);
   }
}
