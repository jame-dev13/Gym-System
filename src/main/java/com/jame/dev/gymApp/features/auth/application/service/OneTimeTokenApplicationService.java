package com.jame.dev.gymApp.features.auth.application.service;

import com.jame.dev.gymApp.domain.exception.MissMatchException;
import com.jame.dev.gymApp.domain.exception.OTTNotFoundException;
import com.jame.dev.gymApp.features.auth.domain.exception.ExpiredException;
import com.jame.dev.gymApp.features.auth.domain.exception.UnverifiedOTTException;
import com.jame.dev.gymApp.features.user.domain.exception.UserEntityNotFoundException;
import com.jame.dev.gymApp.features.auth.domain.model.OneTimeTokenEntity;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import com.jame.dev.gymApp.features.auth.api.request.TokenIdResetPasswordRequest;
import com.jame.dev.gymApp.features.auth.api.request.PasswordResetRequest;
import com.jame.dev.gymApp.features.auth.application.contract.OneTimeTokenRepository;
import com.jame.dev.gymApp.features.user.domain.repository.UserRepository;
import com.jame.dev.gymApp.features.auth.application.contract.OneTimeTokenService;
import com.jame.dev.gymApp.application.contract.TokenDBHasherService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class OneTimeTokenApplicationService implements OneTimeTokenService {

   private final OneTimeTokenRepository repository;
   private final UserRepository userRepository;
   private final TokenDBHasherService hasherService;
   private final PasswordEncoder passwordEncoder;

   @Override
   @Transactional
   public void saveToken(String rawToken, UserEntity user) {
      repository.deleteByUserId(user.getId());
      final OneTimeTokenEntity oneTimeTokenEntity = new OneTimeTokenEntity();
      oneTimeTokenEntity.setUser(user);
      oneTimeTokenEntity.setHashToken(hasherService.hashToken(rawToken));
      oneTimeTokenEntity.setExpiresAt(Instant.now().plusSeconds(600L));
      repository.saveAndFlush(oneTimeTokenEntity);
   }

   @Override
   @Transactional
   public void validateTokenRequest(
      final TokenIdResetPasswordRequest tokenIdResetPasswordRequest) {
      final OneTimeTokenEntity resetTokenEntity = repository
         .findByUserId(tokenIdResetPasswordRequest.uid())
         .orElseThrow(() -> new OTTNotFoundException("No record founded"));

      boolean isExpired = Instant.now()
         .isAfter(resetTokenEntity.getExpiresAt());
      if (isExpired) {
         throw new ExpiredException("Token is expired");
      }

      boolean hashMatches = hasherService.tokenMatches(
         tokenIdResetPasswordRequest.rawToken(),
         resetTokenEntity.getHashToken()
      );

      if (!hashMatches) {
         throw new MissMatchException("Tokens doesn't match");
      }

      resetTokenEntity.setTokenVerified(Boolean.TRUE);
   }

   @Override
   @Transactional
   public void resetPassword(PasswordResetRequest passwordResetRequest) {
      final UserEntity user = userRepository.findByEmail(passwordResetRequest.email())
         .orElseThrow(() -> new UserEntityNotFoundException("User not found."));

      final var ott = repository.findByUserId(user.getId())
         .orElseThrow(() -> new OTTNotFoundException("Reset password is not allowed."));

      if(!ott.isTokenVerified()) {
         throw new UnverifiedOTTException("Token unchecked.");
      }

      final String newPasswordHashed = passwordEncoder.encode(passwordResetRequest.newPassword());
      user.setPassword(newPasswordHashed);

      repository.deleteByUserId(user.getId());
   }
}
