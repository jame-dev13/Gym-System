package com.jame.dev.gymApp.service.out;

import com.jame.dev.gymApp.entity.OneTimeTokenEntity;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.exception.*;
import com.jame.dev.gymApp.model.dto.auth.TokenIdResetPasswordRequest;
import com.jame.dev.gymApp.model.dto.in.PasswordResetDtoInput;
import com.jame.dev.gymApp.repository.OneTimeTokenRepository;
import com.jame.dev.gymApp.repository.UserRepository;
import com.jame.dev.gymApp.service.in.OneTimeTokenService;
import com.jame.dev.gymApp.service.in.TokenDBHasherService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class OneTimeTokenServiceImp implements OneTimeTokenService {

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
   public void resetPassword(PasswordResetDtoInput passwordResetDtoInput) {
      final UserEntity user = userRepository.findByEmail(passwordResetDtoInput.email())
         .orElseThrow(() -> new UserEntityNotFoundException("User not found."));

      final var ott = repository.findByUserId(user.getId())
         .orElseThrow(() -> new OTTNotFoundException("Reset password is not allowed."));

      if(!ott.isTokenVerified()) {
         throw new UnverifiedOTTException("Token unchecked.");
      }

      final String newPasswordHashed = passwordEncoder.encode(passwordResetDtoInput.newPassword());
      user.setPassword(newPasswordHashed);

      repository.deleteByUserId(user.getId());
   }
}
