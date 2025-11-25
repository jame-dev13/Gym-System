package com.jame.dev.gymApp.exception;

public class MembershipNotFoundException extends RuntimeException {
   public MembershipNotFoundException(String message) {
      super(message);
   }
}
