package com.jame.dev.gymApp.features.auth.application.contract.verification;

public interface VerificationSenderService {
   void sendVerificationEmail(String email, String token);
}
