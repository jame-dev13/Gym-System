package com.jame.dev.gymApp.service.out;

import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.entity.VerificationEntity;
import com.jame.dev.gymApp.exception.VerificationTokenNotFoundException;
import com.jame.dev.gymApp.repository.VerificationRepository;
import com.jame.dev.gymApp.service.in.TokenGeneratorService;
import com.jame.dev.gymApp.service.in.VerificationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VerificationServiceImplementation implements VerificationService {
   private final VerificationRepository verificationRepository;
   private final TokenGeneratorService tokenGeneratorService;

   @Override
   public VerificationEntity save(@NonNull UserEntity user) {
      VerificationEntity verification = VerificationEntity
              .builder()
              .id(tokenGeneratorService.generateToken())
              .user(user)
              .expiration(Instant.now().plus(10, ChronoUnit.MINUTES))
              .verified(false)
              .build();
      return verificationRepository.save(verification);
   }

   @Override
   public boolean verify(@NonNull String token) {
      final VerificationEntity entity = verificationRepository.findById(token)
              .orElseThrow(() -> new VerificationTokenNotFoundException("Token not found: " + token));

      final String extractToken = entity.getId();

      final boolean isSameToken = extractToken.equals(token);
      final boolean isValid = Instant.now().isBefore(entity.getExpiration());

      if (isSameToken && isValid) {
         entity.setVerified(true);
         verificationRepository.save(entity);
         return true;
      }
      return false;
   }

   @Override
   public void delete(@NonNull String token) {
      Optional<VerificationEntity> entityOptional = verificationRepository.findById(token);
      if(entityOptional.isPresent()){
         VerificationEntity verificationEntity = entityOptional.get();
         if(verificationEntity.isVerified()) return;
         verificationRepository.deleteById(token);
      }
   }
}
