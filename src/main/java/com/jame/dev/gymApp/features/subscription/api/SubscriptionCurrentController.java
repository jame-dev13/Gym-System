package com.jame.dev.gymApp.features.subscription.api;

import com.jame.dev.gymApp.features.auth.domain.model.AuthPrincipal;
import com.jame.dev.gymApp.features.subscription.api.request.PaymentRequest;
import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionCurrentRequest;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionCheckoutResponse;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionSessionResponse;
import com.jame.dev.gymApp.features.subscription.application.contract.StripeCheckoutService;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.CreatePaymentUseCase;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.current.CreateCurrentSubscriptionUseCase;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.current.DeleteCurrentSubscriptionUseCase;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.current.FinalizeCurrentSubscriptionUseCase;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.current.RenewCurrentSubscriptionUseCase;
import com.jame.dev.gymApp.features.subscription.application.usecases.query.GetSubscriptionByCurrentUseCase;
import com.jame.dev.gymApp.infrastructure.annotation.NotNullObject;
import com.jame.dev.gymApp.infrastructure.async.AsyncResolver;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/app/v1/subscriptions/current")
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
@Validated
public class SubscriptionCurrentController {

   private final GetSubscriptionByCurrentUseCase subscriptionByCurrentUseCase;
   private final CreateCurrentSubscriptionUseCase subscriptionCreate;
   private final RenewCurrentSubscriptionUseCase subscriptionRenew;
   private final FinalizeCurrentSubscriptionUseCase subscriptionFinalize;
   private final DeleteCurrentSubscriptionUseCase subscriptionDelete;
   private final StripeCheckoutService stripeCheckoutService;
   private final CreatePaymentUseCase createPaymentUseCase;

   @GetMapping
   public ResponseEntity<SubscriptionResponse> getCurrentSub (final @AuthenticationPrincipal AuthPrincipal principal) {
      return ResponseEntity.ok(subscriptionByCurrentUseCase.getCurrent(principal));
   }

   @PostMapping
   public ResponseEntity<SubscriptionSessionResponse> create(
      @Valid
      @RequestBody
      @NotNullObject final SubscriptionCurrentRequest request,
      final @AuthenticationPrincipal AuthPrincipal principal) {
      final SubscriptionCheckoutResponse checkoutResponse = AsyncResolver.getResult(
         () -> stripeCheckoutService.createCheckoutSessionFrom(principal, request), 20);

      final SubscriptionResponse subscription = subscriptionCreate.create(principal, request);

      final PaymentRequest paymentRequest = PaymentRequest.builder()
         .sessionId(checkoutResponse.sessionId())
         .intentId(checkoutResponse.paymentIndent())
         .subscriptionSessionId(checkoutResponse.paymentSubscription())
         .isPhysical(false)
         .subscriptionId(subscription.id())
         .build();

      createPaymentUseCase.create(paymentRequest);

      final URI location = ServletUriComponentsBuilder
         .fromCurrentRequest()
         .build()
         .toUri();

      final SubscriptionSessionResponse body = new SubscriptionSessionResponse(checkoutResponse, subscription);

      return ResponseEntity.created(location).body(body);
   }

   @PutMapping
   public ResponseEntity<SubscriptionSessionResponse> renew(
      final @AuthenticationPrincipal AuthPrincipal principal,
      @Valid
      @RequestBody
      @NotNullObject final SubscriptionCurrentRequest request) {

      final SubscriptionCheckoutResponse checkoutResponse = AsyncResolver.getResult(
         () -> stripeCheckoutService.createCheckoutSessionFrom(principal, request), 20);

      final SubscriptionResponse subscription = subscriptionRenew.renew(principal, request);

      final PaymentRequest paymentRequest = PaymentRequest.builder()
         .sessionId(checkoutResponse.sessionId())
         .intentId(checkoutResponse.paymentIndent())
         .subscriptionSessionId(checkoutResponse.paymentSubscription())
         .isPhysical(false)
         .subscriptionId(subscription.id())
         .build();

      createPaymentUseCase.create(paymentRequest);

      return ResponseEntity.ok(new SubscriptionSessionResponse(checkoutResponse, subscription));
   }

   @PatchMapping
   public ResponseEntity<SubscriptionResponse> finalizeSubscription(final @AuthenticationPrincipal AuthPrincipal principal) {
      log.info("HIT PATCH finalizeSubscription");
      final SubscriptionResponse response = subscriptionFinalize.finalizeCurrent(principal);
      return ResponseEntity.ok(response);
   }

   @DeleteMapping
   public ResponseEntity<Void> dropSubscription(final @AuthenticationPrincipal AuthPrincipal principal) {
      subscriptionDelete.delete(principal);
      return ResponseEntity.noContent().build();
   }
}
