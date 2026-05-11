package com.jame.dev.gymApp.features.auth.domain.exception;

public class CantSaveVerifcationEntityException extends RuntimeException {
   public CantSaveVerifcationEntityException(String message) {
      super(message);
   }
}
