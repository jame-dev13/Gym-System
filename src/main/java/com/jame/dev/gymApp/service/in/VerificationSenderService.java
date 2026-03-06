package com.jame.dev.gymApp.service.in;

public interface VerificationSenderService {
   void sendVerificationEmail(String email, String token);
}
