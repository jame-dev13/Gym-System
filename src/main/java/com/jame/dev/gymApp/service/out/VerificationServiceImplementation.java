package com.jame.dev.gymApp.service.out;

import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.entity.VerificationEntity;
import com.jame.dev.gymApp.exception.UserNotFoundException;
import com.jame.dev.gymApp.exception.VerificationEntityNotFoundException;
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
      final VerificationEntity entity = verificationRepository.findByUser_Email(email)
              .orElseThrow(() -> new UserNotFoundException("%s email user not found: ".formatted(email)));
      if (entity.isVerified()) {
         return buildVerification(email, true, "Already Verified.");
      }
      final String extractToken = entity.getId();
      log.info("Extract Token: {}", extractToken);
      log.info("Entry token: {}", token);

      final boolean isSameToken = extractToken.equals(token);
      log.info("Is same token: {}", isSameToken);
      final boolean isValid = Instant.now().isBefore(entity.getExpiration());

      if (isSameToken && isValid) {
         entity.setVerified(true);
         verificationRepository.save(entity);
         return buildVerification(email, true, "Verified.");
      }
      final String msg = (isSameToken) ? "Time expired." : "Token is not the same.";
      return buildVerification(email, false, msg);
   }

   @Override
   public void delete(@NonNull String token) {
      final VerificationEntity verification = verificationRepository.findById(token)
              .orElseThrow(() -> new VerificationEntityNotFoundException("%s doesn't match with any record verification."));
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
                                                      final String msg){
     return VerificationDto.builder()
             .timestamp(OffsetDateTime.now())
             .email(email)
             .verified(verified)
             .msg(msg)
             .build();
   }
}
