package com.jame.dev.gymApp.features.auth.domain.exception;

public class InvalidAuthenticationPrincipalException extends RuntimeException {
   public InvalidAuthenticationPrincipalException(String message) {
      super(message);
   }

   public InvalidAuthenticationPrincipalException() {
      super("Principal type unknown.");
   }
}
