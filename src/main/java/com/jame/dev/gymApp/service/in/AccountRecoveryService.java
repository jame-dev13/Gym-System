package com.jame.dev.gymApp.service.in;

public interface AccountRecoveryService {
   void reActivateUserAccount(final String userEmail, final String token);
   void reactivateCustomerAccount(final String userEmail, final String token);
   boolean accountExists(final String userEmail);
}
