package com.jame.dev.gymApp.features.notification.infrastructure.listener;

import com.jame.dev.gymApp.features.auth.application.contract.verification.VerificationSenderService;
import com.jame.dev.gymApp.features.notification.domain.event.VerificationSenderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VerificationSenderListener {
   private final VerificationSenderService verificationSenderService;

   @EventListener(VerificationSenderEvent.class)
   public void sendVerificationEvent(VerificationSenderEvent event) {
      verificationSenderService.sendVerificationEmail(event.email(), event.token());
   }

}
