package com.jame.dev.gymApp.exception;

public class AuthProviderNotAllowedException extends RuntimeException {
   public AuthProviderNotAllowedException(String message) {
      super(message);
   }
}
