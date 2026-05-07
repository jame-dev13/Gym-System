package com.jame.dev.gymApp.observers;

import com.jame.dev.gymApp.exception.UserNotFoundException;
import com.jame.dev.gymApp.messages.service.EmailService;
import com.jame.dev.gymApp.messages.service.HtmlTemplates;
import com.jame.dev.gymApp.model.listeners.PasswordResetEvent;
import com.jame.dev.gymApp.model.messages.EmailDetails;
import com.jame.dev.gymApp.repository.UserRepository;
import com.jame.dev.gymApp.service.in.OneTimeTokenService;
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
