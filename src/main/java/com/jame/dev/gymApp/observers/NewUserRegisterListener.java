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
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
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
                 log.info("New user from signUp to notify detected.");
                 final String rawToken = tokenGeneratorService.generateToken();
                 final VerificationEntity verification = verificationService.save(user.getId(), rawToken);

                 if (Objects.isNull(verification)) {
                    throw new CantSaveVerifcationEntityException("Can't save the verification.");
                 }

                 verificationSenderService.sendVerificationEmail(user.getEmail(), rawToken);
              });
   }

   @EventListener
   @Async
   public void verifyAndNotify(final UserNotifiable user) {
      if (!user.isNotifiable()) return;
      userService.getUserByEmail(user.input().email())
              .ifPresent(u -> {
                 final var rawToken = tokenGeneratorService.generateToken();
                 final var verificationEntity = verificationService.save(u.getId(), rawToken);
                 verificationService.verify(
                         verificationEntity.getUser().getEmail(),
                         rawToken
                 );
                 final var subject = verificationEntity.getUser().getEmail();
                 final var emailDetails = new EmailDetails(
                         subject,
                         HtmlTemplates.adminTemplate(
                                 subject,
                                 user.input().password()),
                         "Welcome."
                 );
                 emailService.sendSimpleEmail(emailDetails);
              });
   }
}
