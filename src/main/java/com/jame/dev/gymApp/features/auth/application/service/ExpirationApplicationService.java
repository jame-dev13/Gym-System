package com.jame.dev.gymApp.features.auth.application.service;

import com.jame.dev.gymApp.features.auth.domain.model.VerificationEntity;
import com.jame.dev.gymApp.features.auth.domain.exception.VerificationNotFoundException;
import com.jame.dev.gymApp.features.auth.domain.exception.WindowTimeException;
import com.jame.dev.gymApp.features.auth.domain.repository.VerificationRepository;
import com.jame.dev.gymApp.features.auth.application.contract.expiration.ExpirationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class ExpirationApplicationService implements ExpirationService {
   private final VerificationRepository verificationRepository;

   @Override
   @Transactional
   public void getMoreTimeFor(final String email) {
      final VerificationEntity verification = verificationRepository.findByUser_Email(email)
              .orElseThrow(() -> new VerificationNotFoundException("Verification not found"));

      if (Instant.now().isBefore(verification.getExpiration())) {
         throw new WindowTimeException("Cannot get mote time yet, verification still being valid.");
      }

      verification.setExpiration(Instant.now().plus(10, ChronoUnit.MINUTES));
      verificationRepository.saveAndFlush(verification);
   }
}
