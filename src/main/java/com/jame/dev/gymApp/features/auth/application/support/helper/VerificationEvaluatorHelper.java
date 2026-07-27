package com.jame.dev.gymApp.features.auth.application.support.helper;

import com.jame.dev.gymApp.infrastructure.security.hash.HashExecutor;
import com.jame.dev.gymApp.features.auth.domain.exception.VerificationAttemptFailedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class VerificationEvaluatorHelper {

   private final HashExecutor hasherService;

   public void evaluateVerificationToken(String hashedToken, String rawToken, Instant expiration) {
      final boolean isSameToken = hasherService.verify(rawToken, hashedToken);
      final boolean isValid = Instant.now().isBefore(expiration);

      if (!isSameToken || !isValid) {
         String invalidTokenMsg = "Token invalid.";
         String attemptInvalid = "Invalid attempt.";
         throw new VerificationAttemptFailedException(!isSameToken ? invalidTokenMsg : attemptInvalid);
      }
   }
}
