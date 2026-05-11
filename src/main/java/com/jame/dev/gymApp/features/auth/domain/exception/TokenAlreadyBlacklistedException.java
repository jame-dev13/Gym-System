package com.jame.dev.gymApp.features.auth.domain.exception;

import org.springframework.security.core.AuthenticationException;

public class TokenAlreadyBlacklistedException extends AuthenticationException {
   public TokenAlreadyBlacklistedException(String message) {
      super(message);
   }
}
