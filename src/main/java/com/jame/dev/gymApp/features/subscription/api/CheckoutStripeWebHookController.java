package com.jame.dev.gymApp.features.subscription.api;

import com.jame.dev.gymApp.features.subscription.application.support.handler.StripeWebhookHandler;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/app/v1/checkout")
@RequiredArgsConstructor
@Validated
public class CheckoutStripeWebHookController {

   private final StripeWebhookHandler stripeWebhookHandler;

   @PostMapping("/webhook/stripe")
   public ResponseEntity<Void> handleStripeWebhook(
      final HttpServletRequest request) throws IOException {
      log.info("[HIT]: Hitting stripe webhook.");
      final String payload = new String(request.getInputStream().readAllBytes());
      final String signatureHeader = request.getHeader("Stripe-Signature");
      return stripeWebhookHandler.handle(payload, signatureHeader);
   }
}
