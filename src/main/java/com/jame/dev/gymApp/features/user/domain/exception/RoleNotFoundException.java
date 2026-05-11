package com.jame.dev.gymApp.features.user.domain.exception;

public class RoleNotFoundException extends RuntimeException {
   public RoleNotFoundException(String message) {
      super(message);
   }
}
