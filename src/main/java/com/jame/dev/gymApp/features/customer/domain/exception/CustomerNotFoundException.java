package com.jame.dev.gymApp.features.customer.domain.exception;

public class CustomerNotFoundException extends RuntimeException {
   public CustomerNotFoundException(String message) {
      super(message);
   }

   public CustomerNotFoundException() {
      super("Customer not found.");
   }
}
