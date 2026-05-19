package com.jame.dev.gymApp.features.user.application.listener;

import com.jame.dev.gymApp.application.contract.TokenGeneratorService;
import com.jame.dev.gymApp.features.auth.application.contract.verification.VerificationSenderService;
import com.jame.dev.gymApp.features.auth.application.contract.verification.VerificationService;
import com.jame.dev.gymApp.features.auth.domain.event.UserNotifiableEvent;
import com.jame.dev.gymApp.features.auth.domain.exception.CantSaveVerifcationEntityException;
import com.jame.dev.gymApp.features.auth.domain.model.VerificationEntity;
import com.jame.dev.gymApp.features.notification.application.contract.EmailService;
import com.jame.dev.gymApp.features.notification.application.dto.EmailDetails;
import com.jame.dev.gymApp.features.notification.domain.model.HtmlTemplates;
import com.jame.dev.gymApp.features.user.domain.event.UserRegisteredEvent;
import com.jame.dev.gymApp.features.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class UserRegisterListener {
   private final EmailService emailService;
   private final UserRepository userRepository;
   private final VerificationService verificationService;
   private final TokenGeneratorService tokenGeneratorService;
   private final VerificationSenderService verificationSenderService;

   @EventListener
   @Async("taskExecutor")
   public void verifySignUpUser(final UserRegisteredEvent event) {
      userRepository.findByEmail(event.email())
         .ifPresent(user -> {
            final String rawToken = tokenGeneratorService.generateToken();
            final VerificationEntity verification = verificationService.save(user.getId(), rawToken);

            if (Objects.isNull(verification)) {
               throw new CantSaveVerifcationEntityException("Can't save the verification.");
            }

            verificationSenderService.sendVerificationEmail(user.getEmail(), rawToken);
         });
   }

   @EventListener
   @Async("taskExecutor")
   public void verifyAndNotify(final UserNotifiableEvent user) {
      if (!user.isNotifiable()) return;
      userRepository.findByEmail(user.input().email())
         .ifPresent(u -> {
            final String email = u.getEmail();
            final String rawToken = tokenGeneratorService.generateToken();
            verificationService.save(u.getId(), rawToken);
            verificationService.verify(email, rawToken);
            final var emailDetails = new EmailDetails(
               email,
               HtmlTemplates.adminTemplate(
                  email,
                  user.input().password()),
               "Welcome."
            );
            emailService.sendSimpleEmail(emailDetails);
         });
   }
}
