package com.jame.dev.gymApp.controller.components;

import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.exception.UserEntityNotFoundException;
import com.jame.dev.gymApp.messages.service.EmailService;
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
      final String pwdRaw = dto.password() != null ? dto.password(): "password123";
      final String email = dto.email();
      final UserEntity userEntity = userService.getUserByEmail(email)
              .orElseThrow(() -> new UserEntityNotFoundException("User not found."));
      var verificationEntity = verificationService.save(userEntity);
      var verificationDto = verificationService.verify(verificationEntity.getUser().getEmail(), verificationEntity.getId());
      if(verificationDto.verified()) {
         final String htmlBody = htmlCard().replace("{{email}}", email).replace("{{password}}", pwdRaw);
         emailService.sendSimpleEmail(
                 EmailDetails.builder()
                         .recipient(email)
                         .subject("New Administrator")
                         .msgBody(htmlBody)
                         .build()
         ).thenAccept(sent -> {
            log.info(sent ? "Email sent": "Email not sent");
         });
      }
   }

   private String htmlCard() {
      return """
              <!DOCTYPE html>
              <html>
              
              <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Your Credentials</title>
              </head>
              
              <body
                style="margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f7f9; color: #333;">
                <table border="0" cellpadding="0" cellspacing="0" width="100%">
                  <tr>
                    <td align="center" style="padding: 40px 0;">
                      <table border="0" cellpadding="0" cellspacing="0" width="600"
                        style="background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 10px rgba(0,0,0,0.05);">
                        <tr>
                          <td align="center" style="background-color: #0891b2; padding: 30px 20px;">
                            <h1 style="color: #ffffff; margin: 0; font-size: 24px;">Welcome!</h1>
                          </td>
                        </tr>
              
                        <tr>
                          <td style="padding: 40px 30px;">
                            <p style="font-size: 16px; line-height: 1.6; margin-bottom: 20px;">
                              Hi there! An administrator account has been created for your. Below are you credentials to access the platform.
                            </p>
              
                            <div
                              style="background-color: #f8fafc; border: 1px solid #e2e8f0; border-radius: 6px; padding: 20px; margin-bottom: 30px;">
                              <table width="100%">
                                <tr>
                                  <td style="padding: 5px 0; color: #64748b; font-size: 14px; width: 80px;"><strong>Email:</strong>
                                  </td>
                                  <td style="padding: 5px 0; font-family: monospace; font-size: 16px; color: #0891b2;">{{email}}</td>
                                </tr>
                                <tr>
                                  <td style="padding: 5px 0; color: #64748b; font-size: 14px;"><strong>Password:</strong></td>
                                  <td style="padding: 5px 0; font-family: monospace; font-size: 16px; color: #0891b2;">{{password}}
                                  </td>
                                </tr>
                              </table>
                            </div>
              
                            <table border="0" cellpadding="0" cellspacing="0" width="100%">
                              <tr>
                                <td align="center">
                                  <a href="https://tu-app.com/login"
                                    style="background-color: #0891b2; color: #ffffff; padding: 14px 30px; text-decoration: none; border-radius: 5px; font-weight: bold; display: inline-block;">
                                    Login now.
                                  </a>
                                </td>
                              </tr>
                            </table>
              
                            <p
                              style="font-size: 14px; color: #ef4444; margin-top: 30px; border-top: 1px solid #eee; padding-top: 20px;">
                              <strong>Importante:</strong> For security reasons, we highly recommend to change your password as soon as possible or even after your first login.
                            </p>
                          </td>
                        </tr>
              
                        <tr>
                          <td align="center" style="padding: 20px; background-color: #f1f5f9; color: #94a3b8; font-size: 12px;">
                            <p style="margin: 0;">This is an automated message, please don't reply to this email.</p>
                            <p style="margin: 5px 0 0;">&copy; 2024 GymApp - All rights reserved.</p>
                          </td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                </table>
              </body>
              
              </html>
              """;
   }
}
