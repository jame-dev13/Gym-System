package com.jame.dev.gymApp.features.auth.application.contract.recovery;

public interface AccountRecoveryService {
   void reActivateUserAccount(final String userEmail, final String token);
   boolean accountExists(final String userEmail);
}
