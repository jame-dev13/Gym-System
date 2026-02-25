package com.jame.dev.gymApp.service.out;

import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.entity.VerificationEntity;
import com.jame.dev.gymApp.exception.UserEntityNotFoundException;
import com.jame.dev.gymApp.exception.UserNotFoundException;
import com.jame.dev.gymApp.exception.VerificationEntityNotFoundException;
import com.jame.dev.gymApp.factories.VerificationFactory;
import com.jame.dev.gymApp.model.dto.auth.ExpirationWindowDto;
import com.jame.dev.gymApp.model.dto.auth.VerificationDto;
import com.jame.dev.gymApp.repository.UserRepository;
import com.jame.dev.gymApp.repository.VerificationRepository;
import com.jame.dev.gymApp.service.in.TokenGeneratorService;
import com.jame.dev.gymApp.service.in.VerificationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class VerificationServiceImplementation implements VerificationService {
   private final UserRepository userRepository;
   private final VerificationRepository verificationRepository;
   private final TokenGeneratorService tokenGeneratorService;
   private final VerificationFactory verificationFactory;

   @Override
   @Transactional
   public VerificationEntity save(final long userId) {
      final UserEntity user = userRepository.findById(userId)
              .orElseThrow(() -> new UserEntityNotFoundException("User not found."));
      final String token = tokenGeneratorService.generateToken();
      final VerificationEntity verification = verificationFactory.createVerification(user, token);
      return verificationRepository.saveAndFlush(verification);
   }

   @Override
   @Transactional
   public VerificationDto verify(@NonNull final String email, @NonNull final String token) {
      final VerificationEntity verification = verificationRepository.findByUser_Email(email)
              .orElseThrow(() -> new VerificationEntityNotFoundException("Verification not found: " + email));
      if (verification.isVerified()) {
         return verificationFactory.createDto(email, true, "Already Verified.");
      }
      final String extractToken = verification.getId();

      final boolean isSameToken = extractToken.equals(token);
      final boolean isValid = Instant.now().isBefore(verification.getExpiration());

      if (isSameToken && isValid) {
         verification.setVerified(true);
         verificationRepository.saveAndFlush(verification);
         return verificationFactory.createDto(email, true, "Verified.");
      }
      final String msgExpired = """
              Time expired. You can get more
              on: /auth/verify/get-more-exp-time/%s
              """.formatted(email);
      final String msg = (isSameToken) ? msgExpired : "Token is not the same.";
      return verificationFactory.createDto(email, false, msg);
   }

   @Override
   @Transactional
   public ExpirationWindowDto getMoreExpTime(@NonNull String email) {
      final VerificationEntity verificationEntity = verificationRepository.findByUser_Email(email)
              .orElseThrow(() -> new VerificationEntityNotFoundException("Verification not found: " + email));

      final Instant now = Instant.now();
      final Instant currentExpirationTime = verificationEntity.getExpiration();

      if (now.isBefore(currentExpirationTime)) {
         return verificationFactory.createExpiration(email, false, currentExpirationTime,
                 "Verification still available for %s, Check -> /auth/verify/%s".formatted(email, email));
      }

      final Instant newExpTime = Instant.now().plus(10, ChronoUnit.MINUTES);
      verificationEntity.setExpiration(newExpTime);
      return verificationFactory.createExpiration(email, true, newExpTime, """
              New verification time set. Please checkout -> /auth/verify/%s
              """.formatted(email));
   }

   @Override
   @Transactional
   public void delete(@NonNull String token) {
      final VerificationEntity verification = verificationRepository.findById(token)
              .orElseThrow(() -> new VerificationEntityNotFoundException("Verification entity not found: " + token));
      if (verification.isVerified()) return;
      verificationRepository.deleteById(token);
   }

   @Override
   public boolean isVerified(String email) {
      final VerificationEntity verification = verificationRepository.findByUser_Email(email)
              .orElseThrow(() -> new UserNotFoundException("User with: %s not found.".formatted(email)));
      return verification.isVerified();
   }
}
