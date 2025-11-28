package com.jame.dev.gymApp.exception;

public class TokenAlreadyBlacklistedException extends RuntimeException {
   public TokenAlreadyBlacklistedException(String message) {
      super(message);
   }
}
