package com.jame.dev.gymApp.service.out;

import com.jame.dev.gymApp.entity.VerificationEntity;
import com.jame.dev.gymApp.exception.VerificationAttemptFailedException;
import com.jame.dev.gymApp.exception.VerificationNotFoundException;
import com.jame.dev.gymApp.repository.CustomerRepository;
import com.jame.dev.gymApp.repository.VerificationRepository;
import com.jame.dev.gymApp.service.in.AccountRecoveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountRecoveryServiceImplementation implements AccountRecoveryService {
   private final VerificationRepository verificationRepository;
   private final PasswordEncoder passwordEncoder;
   private final CustomerRepository customerRepository;

   private @NonNull VerificationEntity getVerificationEntity(String userEmail, String token) {
      final VerificationEntity VE = verificationRepository.findDeactivatedByUser_Email(userEmail)
              .orElseThrow(() -> new VerificationNotFoundException("Verified user account's not found."));
      boolean tokenMatch = passwordEncoder.matches(token, VE.getToken());
      if (!tokenMatch) {
         throw new VerificationAttemptFailedException("Token mismatch.");
      }
      return VE;
   }

   @Override
   @Transactional
   public void reActivateUserAccount(String userEmail, String token) {
      final VerificationEntity VE = getVerificationEntity(userEmail, token);
      final var userEntity = VE.getUser();
      log.info("{}", userEntity);
      userEntity.setActive(true);
      userEntity.setUpdatedAt(Instant.now());
   }

   @Override
   @Transactional
   public void reactivateCustomerAccount(String userEmail, String token) {
      final VerificationEntity VE = getVerificationEntity(userEmail, token);

      final var userEntity = VE.getUser();
      customerRepository.findDeactivatedByUser_email(userEntity.getEmail())
              .ifPresent(CE -> {
                 CE.setActive(true);
                 CE.setUpdatedAt(Instant.now());
                 CE.setUser(userEntity);
              });
   }

   @Override
   public boolean accountExists(String userEmail) {
      return verificationRepository.existsDeactivatedByUser_Email(userEmail);
   }
}
