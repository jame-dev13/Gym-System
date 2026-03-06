package com.jame.dev.gymApp.factories;

import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.entity.VerificationEntity;
import com.jame.dev.gymApp.model.dto.auth.VerificationDto;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class VerificationFactory {

   public VerificationEntity createVerification(
           final UserEntity user, final String token) {
      return VerificationEntity.builder()
              .user(user)
              .token(token)
              .expiration(Instant.now().plus(10, ChronoUnit.MINUTES))
              .verified(false)
              .build();
   }

   public VerificationDto createDtoFrom(
           final VerificationEntity verificationEntity) {
      return VerificationDto.builder()
              .timestamp(OffsetDateTime.now())
              .email(verificationEntity.getUser().getEmail())
              .verified(verificationEntity.isVerified())
              .msg("User verified.")
              .build();
   }
}
