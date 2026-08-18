package com.jame.dev.gymApp.features.auth.infrastructure.listeners;

import com.jame.dev.gymApp.features.auth.application.support.factory.VerificationFactory;
import com.jame.dev.gymApp.features.auth.domain.event.VerifyOauthUserEvent;
import com.jame.dev.gymApp.features.auth.domain.model.CustomOAuth2User;
import com.jame.dev.gymApp.features.auth.domain.model.VerificationEntity;
import com.jame.dev.gymApp.features.auth.domain.repository.VerificationRepository;
import com.jame.dev.gymApp.features.user.domain.exception.UserEntityNotFoundException;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import com.jame.dev.gymApp.features.user.domain.repository.UserQueryRepository;
import com.jame.dev.gymApp.infrastructure.security.hash.HashExecutor;
import com.jame.dev.gymApp.infrastructure.security.token.TokenGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class VerificationOauth2ListenerSaver {
   private final VerificationFactory verificationFactory;
   private final VerificationRepository verificationRepository;
   private final HashExecutor hasherService;
   private final TokenGeneratorService tokenGeneratorService;
   private final UserQueryRepository userQueryRepository;

   @Async("taskExecutor")
   @EventListener(VerifyOauthUserEvent.class)
   @Transactional
   public void onCustomOauth2Created(final VerifyOauthUserEvent e) {
      final CustomOAuth2User user = e.oAuth2User();
      if (verificationRepository.existsByUser_EmailAndVerifiedTrue(user.username()))
         return;

      final UserEntity userEntity = userQueryRepository.findById(user.id())
         .orElseThrow(() -> new UserEntityNotFoundException("User not found." + user.id()));

      final String token = tokenGeneratorService.generateToken();
      final VerificationEntity verification = verificationFactory.createVerification(
         userEntity, hasherService.hash(token));
      verification.setVerified(true);
      verificationRepository.saveAndFlush(verification);
   }
}
