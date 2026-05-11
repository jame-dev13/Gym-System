package com.jame.dev.gymApp.features.auth.domain.exception;

public class AuthProviderNotAllowedException extends RuntimeException {
   public AuthProviderNotAllowedException(String message) {
      super(message);
   }
}
