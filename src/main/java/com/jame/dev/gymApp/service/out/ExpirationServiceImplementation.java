package com.jame.dev.gymApp.service.out;

import com.jame.dev.gymApp.entity.VerificationEntity;
import com.jame.dev.gymApp.exception.VerificationNotFoundException;
import com.jame.dev.gymApp.exception.WindowTimeException;
import com.jame.dev.gymApp.repository.VerificationRepository;
import com.jame.dev.gymApp.service.in.ExpirationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class ExpirationServiceImplementation implements ExpirationService {
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
