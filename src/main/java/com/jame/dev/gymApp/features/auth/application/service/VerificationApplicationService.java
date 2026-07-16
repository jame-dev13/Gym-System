package com.jame.dev.gymApp.features.auth.application.service;

import com.jame.dev.gymApp.infrastructure.security.hash.TokenDBHasherService;
import com.jame.dev.gymApp.features.auth.application.contract.verification.VerificationService;
import com.jame.dev.gymApp.features.auth.application.support.factory.VerificationFactory;
import com.jame.dev.gymApp.features.auth.application.support.helper.VerificationEvaluatorHelper;
import com.jame.dev.gymApp.features.auth.domain.exception.AlreadyVerifiedException;
import com.jame.dev.gymApp.features.auth.domain.exception.VerificationNotFoundException;
import com.jame.dev.gymApp.features.auth.domain.model.VerificationEntity;
import com.jame.dev.gymApp.features.auth.domain.repository.VerificationRepository;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import com.jame.dev.gymApp.infrastructure.annotation.EmailValid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationApplicationService implements VerificationService {
   private final VerificationRepository verificationRepository;
   private final VerificationFactory verificationFactory;
   private final VerificationEvaluatorHelper verificationEvaluatorHelper;
   private final TokenDBHasherService tokenHasherService;

   @Override
   @Transactional
   public void verify(VerificationEntity verification, String rawToken) {
      if (verification.isVerified())
         throw new AlreadyVerifiedException("Account has already been verified.");

      verificationEvaluatorHelper.evaluateVerificationToken(
         verification.getToken(),
         rawToken,
         verification.getExpiration()
      );

      verification.setVerified(true);
      verificationRepository.saveAndFlush(verification);
   }

   @Override
   @Transactional
   public void verify(final String email, final String rawToken) {
      final VerificationEntity verification = verificationRepository.findByUser_Email(email)
         .orElseThrow(() -> new VerificationNotFoundException("Verification record not found for: " + email));

      if (verification.isVerified())
         throw new AlreadyVerifiedException("Account has already been verified.");

      verificationEvaluatorHelper.evaluateVerificationToken(
         verification.getToken(),
         rawToken,
         verification.getExpiration()
      );

      verification.setVerified(true);
      verificationRepository.saveAndFlush(verification);
   }

   @Override
   @Transactional
   public VerificationEntity save(UserEntity user, String token) {
      final VerificationEntity verification = verificationFactory.createVerification(user, tokenHasherService.hashToken(token));
      return verificationRepository.saveAndFlush(verification);
   }

   @Override
   public VerificationEntity getByUserEmail(String email) {
      return verificationRepository.findByUser_Email(email)
         .orElseThrow(() -> new VerificationNotFoundException("User without verification."));
   }

   @Override
   public VerificationEntity getByDeactivatedUserEmail(String email) {
      return verificationRepository.findDeactivatedByUser_Email(email)
         .orElseThrow(() -> new VerificationNotFoundException("User without verification."));
   }

   @Override
   @Transactional
   public void update(VerificationEntity verificationEntity, String rawToken) {
      verificationEntity.setToken(tokenHasherService.hashToken(rawToken));
      verificationEntity
         .setExpiration(Instant.now().plus(10, ChronoUnit.MINUTES)
      );
      verificationRepository.saveAndFlush(verificationEntity);
   }

   @Override
   public boolean isVerified(@EmailValid String email) {
      return verificationRepository.existsByUser_EmailAndVerifiedTrue(email);
   }

   @Override
   public boolean checkVerifiedDeactivated(String email) {
      return verificationRepository.existsDeactivatedByUser_Email(email);
   }

   @Override
   public boolean verificationExistsFor(String email) {
      return verificationRepository.existsByUser_Email(email);
   }

}
