package com.jame.dev.gymApp.observers;

import com.jame.dev.gymApp.entity.VerificationEntity;
import com.jame.dev.gymApp.exception.CantSaveVerifcationEntityException;
import com.jame.dev.gymApp.messages.service.EmailService;
import com.jame.dev.gymApp.messages.service.HtmlTemplates;
import com.jame.dev.gymApp.model.listeners.UserNotifiable;
import com.jame.dev.gymApp.model.listeners.UserRegisteredEvent;
import com.jame.dev.gymApp.model.messages.EmailDetails;
import com.jame.dev.gymApp.service.in.TokenGeneratorService;
import com.jame.dev.gymApp.service.in.UserService;
import com.jame.dev.gymApp.service.in.VerificationSenderService;
import com.jame.dev.gymApp.service.in.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class NewUserRegisterListener {
   private final EmailService emailService;
   private final UserService userService;
   private final VerificationService verificationService;
   private final TokenGeneratorService tokenGeneratorService;
   private final VerificationSenderService verificationSenderService;

   @EventListener
   @Async("taskExecutor")
   public void verifySignUpUser(final UserRegisteredEvent event) {
      userService.getUserByEmail(event.email())
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
   public void verifyAndNotify(final UserNotifiable user) {
      if (!user.isNotifiable()) return;
      userService.getUserByEmail(user.input().email())
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
