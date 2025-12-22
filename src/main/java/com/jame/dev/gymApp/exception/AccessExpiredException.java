package com.jame.dev.gymApp.exception;

import org.springframework.security.core.AuthenticationException;

public class AccessExpiredException extends AuthenticationException {
   public AccessExpiredException(String message) {
      super(message);
   }
}
