package com.jame.dev.gymApp.observers;

import com.jame.dev.gymApp.messages.service.EmailService;
import com.jame.dev.gymApp.messages.service.HtmlTemplates;
import com.jame.dev.gymApp.model.listeners.UserNotifiable;
import com.jame.dev.gymApp.model.messages.EmailDetails;
import com.jame.dev.gymApp.service.in.TokenGeneratorService;
import com.jame.dev.gymApp.service.in.UserService;
import com.jame.dev.gymApp.service.in.VerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewUserRegisterListener {
   private final EmailService emailService;
   private final UserService userService;
   private final VerificationService verificationService;
   private final TokenGeneratorService tokenGeneratorService;

   @EventListener
   @Async
   public void verifyAndNotify(final UserNotifiable user) {
      if (!user.isNotifiable()) return;
      userService.getUserByEmail(user.input().email())
              .ifPresent(u -> {
                 final var rawToken = tokenGeneratorService.generateToken();
                 final var verificationEntity = verificationService.save(u.getId(), rawToken);
                 final var verificationDto = verificationService.verify(
                         verificationEntity.getUser().getEmail(),
                         rawToken
                 );
                 final var subject = verificationDto.email();
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
