package com.jame.dev.gymApp.service.out;

import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.entity.VerificationEntity;
import com.jame.dev.gymApp.exception.UserNotFoundException;
import com.jame.dev.gymApp.exception.VerificationEntityNotFoundException;
import com.jame.dev.gymApp.model.dto.auth.ExpirationWindowDto;
import com.jame.dev.gymApp.model.dto.auth.VerificationDto;
import com.jame.dev.gymApp.repository.VerificationRepository;
import com.jame.dev.gymApp.service.in.TokenGeneratorService;
import com.jame.dev.gymApp.service.in.VerificationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationServiceImplementation implements VerificationService {
   private final VerificationRepository verificationRepository;
   private final TokenGeneratorService tokenGeneratorService;

   @Override
   public VerificationEntity save(@NonNull UserEntity user) {
      final VerificationEntity verification = VerificationEntity.builder()
              .id(tokenGeneratorService.generateToken())
              .user(user)
              .expiration(Instant.now().plus(10, ChronoUnit.MINUTES))
              .verified(false)
              .build();
      return verificationRepository.save(verification);
   }

   @Override
   public VerificationDto verify(@NonNull final String email, @NonNull final String token) {
      final VerificationEntity verification = getVerificationEntity(email);
      if (verification.isVerified()) {
         return buildVerification(email, true, "Already Verified.");
      }
      final String extractToken = verification.getId();
      log.info("Extract Token: {}", extractToken);
      log.info("Entry token: {}", token);

      final boolean isSameToken = extractToken.equals(token);
      log.info("Is same token: {}", isSameToken);
      final boolean isValid = Instant.now().isBefore(verification.getExpiration());

      if (isSameToken && isValid) {
         verification.setVerified(true);
         verificationRepository.save(verification);
         return buildVerification(email, true, "Verified.");
      }
      final String msgExpired = """
              Time expired. You can get more
              on: /auth/verify/get-more-exp-time
              """;
      final String msg = (isSameToken) ? msgExpired : "Token is not the same.";
      return buildVerification(email, false, msg);
   }

   @Override
   public ExpirationWindowDto getMoreExpTime(@NonNull String email) {
      final VerificationEntity verificationEntity = getVerificationEntity(email);
      final Instant now = Instant.now();
      final Instant currentExpirationTime = verificationEntity.getExpiration();
      if (now.isBefore(currentExpirationTime)) {
         return buildExpiration(email, false, currentExpirationTime,
                 "Verification still available for %s, Check -> /auth/verify/%s".formatted(email, email));
      }
      final Instant newExpTime = Instant.now().plus(10, ChronoUnit.MINUTES);
      verificationEntity.setExpiration(newExpTime);
      return buildExpiration(email, true, newExpTime, """
              New verification time set. Please checkout -> /auth/verify/%s
              """.formatted(email));
   }

   @Override
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

   private @NonNull VerificationDto buildVerification(final String email,
                                                      final boolean verified,
                                                      final String msg) {
      return VerificationDto.builder()
              .timestamp(OffsetDateTime.now())
              .email(email)
              .verified(verified)
              .msg(msg)
              .build();
   }

   private @NonNull ExpirationWindowDto buildExpiration(final String email,
                                                        boolean updated,
                                                        final Instant expiresAt,
                                                        final String msg) {
      return ExpirationWindowDto.builder()
              .requestAt(OffsetDateTime.now())
              .email(email)
              .updated(updated)
              .state((updated) ? "Time Updated" : "No changed")
              .expiresAt(OffsetDateTime.ofInstant(expiresAt, ZoneId.systemDefault()))
              .msg(msg)
              .build();
   }

   private VerificationEntity getVerificationEntity(@NonNull final String email) {
      return verificationRepository.findByUser_Email(email)
              .orElseThrow(() -> new VerificationEntityNotFoundException("Verification not found: " + email));
   }
}
