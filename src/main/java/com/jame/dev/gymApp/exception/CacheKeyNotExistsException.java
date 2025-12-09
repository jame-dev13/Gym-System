package com.jame.dev.gymApp.exception;

public class CacheKeyNotExistsException extends RuntimeException {
   public CacheKeyNotExistsException(String message) {
      super(message);
   }
}
