package com.jame.dev.gymApp.features.subscription.domain.exception;

public class MembershipNotFoundException extends RuntimeException {
   public MembershipNotFoundException(String message) {
      super(message);
   }
}
