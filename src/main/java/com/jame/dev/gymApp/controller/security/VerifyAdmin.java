package com.jame.dev.gymApp.controller.security;

import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.exception.UserEntityNotFoundException;
import com.jame.dev.gymApp.messages.service.EmailService;
import com.jame.dev.gymApp.messages.service.HtmlTemplates;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
import com.jame.dev.gymApp.model.messages.EmailDetails;
import com.jame.dev.gymApp.service.in.UserService;
import com.jame.dev.gymApp.service.in.VerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class VerifyAdmin {
   private final VerificationService verificationService;
   private final UserService userService;
   private final EmailService emailService;

   public void verifyAndApproveAdmin(UserDtoInput dto) {
      final String pwdRaw = dto.password() != null ? dto.password() : "password123";
      final String email = dto.email();
      final UserEntity userEntity = userService.getUserByEmail(email)
              .orElseThrow(() -> new UserEntityNotFoundException("User not found."));
      var verificationEntity = verificationService.save(userEntity.getId());
      var verificationDto = verificationService.verify(verificationEntity.getUser().getEmail(), verificationEntity.getId());
      if (verificationDto.verified()) {
         final String htmlBody = HtmlTemplates.adminCredentials()
                 .replace("{{email}}", email)
                 .replace("{{password}}", pwdRaw);
         emailService.sendSimpleEmail(
                 EmailDetails.builder()
                         .recipient(email)
                         .subject("New Administrator")
                         .msgBody(htmlBody)
                         .build()
         ).thenAccept(sent -> {
            log.info(sent ? "Email sent" : "Email not sent");
         });
      }
   }
}