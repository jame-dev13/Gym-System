package com.jame.dev.gymApp.features.notification.application.support.factory;

import com.jame.dev.gymApp.features.notification.application.dto.EmailDetails;

public final class EmailDetailsFactory {

   public static EmailDetails createDetailsFrom(
           final String recipient, final String subject, final String body) {
      return EmailDetails.builder()
              .recipient(recipient)
              .msgBody(body)
              .subject(subject)
              .build();
   }
}
