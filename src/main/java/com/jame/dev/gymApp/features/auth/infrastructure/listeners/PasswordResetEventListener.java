package com.jame.dev.gymApp.features.auth.infrastructure.listeners;

import com.jame.dev.gymApp.features.user.domain.exception.UserNotFoundException;
import com.jame.dev.gymApp.features.notification.application.contract.EmailService;
import com.jame.dev.gymApp.features.notification.domain.model.HtmlTemplates;
import com.jame.dev.gymApp.features.auth.domain.event.PasswordResetEvent;
import com.jame.dev.gymApp.features.notification.application.dto.EmailDetails;
import com.jame.dev.gymApp.features.user.infrastructure.persistence.UserRepository;
import com.jame.dev.gymApp.features.auth.application.contract.OneTimeTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PasswordResetEventListener {

   private final OneTimeTokenService oneTimeTokenService;
   private final UserRepository userRepository;
   private final EmailService emailService;
   private final static String MAGIC_URL = "http://localhost:8080/auth/passwords/reset?token={{token}}&&uid={{uid}}";

   @Transactional
   @EventListener(PasswordResetEvent.class)
   public void savePasswordRequest(PasswordResetEvent passwordResetEvent) {
      final String email = passwordResetEvent.email();
      final String token = passwordResetEvent.rawToken();
      final var userEntity = userRepository.findByEmail(email)
         .orElseThrow(() -> new UserNotFoundException("User not found."));
      oneTimeTokenService.saveToken(token, userEntity);


      final EmailDetails emailDetails = new EmailDetails(
         email,
         HtmlTemplates.magicUrlTemplate(
            MAGIC_URL
               .replace("{{token}}", token)
               .replace("{{uid}}", userEntity.getId().toString())),
         "Password Reset"
      );

      emailService.sendSimpleEmail(emailDetails);
   }
}
