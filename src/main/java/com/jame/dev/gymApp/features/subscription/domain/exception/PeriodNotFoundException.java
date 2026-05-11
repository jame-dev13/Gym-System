package com.jame.dev.gymApp.features.subscription.domain.exception;

public class PeriodNotFoundException extends RuntimeException {
   public PeriodNotFoundException(String message) {
      super(message);
   }
}
