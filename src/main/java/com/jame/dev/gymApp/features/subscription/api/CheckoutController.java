package com.jame.dev.gymApp.features.subscription.api;

import com.jame.dev.gymApp.application.contract.IdentityExtractorService;
import com.jame.dev.gymApp.features.subscription.api.request.CheckoutRequest;
import com.jame.dev.gymApp.features.subscription.api.response.CheckoutResponse;
import com.jame.dev.gymApp.features.subscription.application.contract.StripeCheckoutService;
import com.jame.dev.gymApp.features.subscription.application.service.StripeWebhookHandler;
import com.jame.dev.gymApp.infrastructure.annotation.NotNullObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/app/v1/checkout")
@RequiredArgsConstructor
@Validated
public class CheckoutController {

   private final StripeCheckoutService stripeCheckoutService;
   private final StripeWebhookHandler stripeWebhookHandler;
   private final IdentityExtractorService extractorService;

   @PreAuthorize("hasRole('USER')")
   @PostMapping
   public ResponseEntity<CheckoutResponse> createCheckoutSession(
      @Valid @RequestBody @NotNullObject final CheckoutRequest request,
      final Authentication authentication) {
      final String customerEmail = extractorService.extract(authentication);
      final CheckoutResponse response = stripeCheckoutService.createCheckoutSession(request, customerEmail);
      return ResponseEntity.ok(response);
   }

   @PostMapping("/webhook/stripe")
   public ResponseEntity<Void> handleStripeWebhook(
      final HttpServletRequest request) throws IOException {
      final String payload = new String(request.getInputStream().readAllBytes());
      final String signatureHeader = request.getHeader("Stripe-Signature");
      return stripeWebhookHandler.handle(payload, signatureHeader);
   }
}
