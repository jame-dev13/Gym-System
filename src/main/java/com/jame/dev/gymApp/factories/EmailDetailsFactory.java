package com.jame.dev.gymApp.factories;

import com.jame.dev.gymApp.model.messages.EmailDetails;

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
