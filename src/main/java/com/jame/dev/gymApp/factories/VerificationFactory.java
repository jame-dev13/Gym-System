package com.jame.dev.gymApp.factories;

import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.entity.VerificationEntity;
import com.jame.dev.gymApp.model.dto.auth.ExpirationWindowDto;
import com.jame.dev.gymApp.model.dto.auth.VerificationDto;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@Component
public class VerificationFactory {

   public VerificationEntity createVerification(
           final UserEntity user, final String token) {
      return VerificationEntity.builder()
              .id(token)
              .user(user)
              .expiration(Instant.now().plus(10, ChronoUnit.MINUTES))
              .verified(false)
              .build();
   }

   public VerificationDto createDto(
           final String email, boolean isVerified, final String msg) {
      return VerificationDto.builder()
              .timestamp(OffsetDateTime.now())
              .email(email)
              .verified(isVerified)
              .msg(msg)
              .build();
   }

   public ExpirationWindowDto createExpiration(
           final String email, boolean updated, final Instant expiresAt, final String msg) {
      return ExpirationWindowDto.builder()
              .requestAt(OffsetDateTime.now())
              .email(email)
              .updated(updated)
              .state(updated ? "Time Updated" : "Not changed")
              .expiresAt(OffsetDateTime.ofInstant(expiresAt, ZoneId.systemDefault()))
              .msg(msg)
              .build();
   }
}
