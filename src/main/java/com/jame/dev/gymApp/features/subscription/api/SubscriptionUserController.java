package com.jame.dev.gymApp.features.subscription.api;

import com.jame.dev.gymApp.application.contract.IdentityExtractorService;
import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.features.subscription.api.request.PaymentRequest;
import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionRequest;
import com.jame.dev.gymApp.features.subscription.api.response.RetryResponse;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionCheckoutResponse;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionSessionResponse;
import com.jame.dev.gymApp.features.subscription.application.contract.StripeCheckoutService;
import com.jame.dev.gymApp.features.subscription.application.support.handler.RetrySubscriptionPaymentHandler;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.*;
import com.jame.dev.gymApp.features.subscription.application.usecases.query.GetAllSubscriptionsByCustomerEmailUseCase;
import com.jame.dev.gymApp.features.subscription.application.usecases.query.GetByEmailSubscriptionUseCase;
import com.jame.dev.gymApp.features.subscription.application.usecases.query.GetByIdSubscriptionUseCase;
import com.jame.dev.gymApp.infrastructure.annotation.EmailValid;
import com.jame.dev.gymApp.infrastructure.annotation.Minimum;
import com.jame.dev.gymApp.infrastructure.annotation.NotNullObject;
import com.jame.dev.gymApp.infrastructure.async.AsyncResolver;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/app/v1/subscriptions")
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
@Validated
public class SubscriptionUserController {
   private final GetByIdSubscriptionUseCase subscriptionGetById;
   private final GetByEmailSubscriptionUseCase subscriptionGetByEmail;
   private final GetAllSubscriptionsByCustomerEmailUseCase subscriptionGetAllByCustomerEmail;
   private final CreateSubscriptionUseCase subscriptionCreate;
   private final RenewSubscriptionUseCase subscriptionRenew;
   private final FinalizeSubscriptionUseCase subscriptionFinalize;
   private final SoftDeleteSubscriptionByIdUseCase deleteById;
   private final StripeCheckoutService stripeCheckoutService;
   private final IdentityExtractorService extractorService;
   private final CreatePaymentUseCase createPaymentUseCase;
   private final RetrySubscriptionPaymentHandler retrySubscriptionPaymentHandler;

   @PostMapping("/retry")
   public ResponseEntity<RetryResponse> retry(final Authentication authentication) {
      final String subject = extractorService.extract(authentication);
      return retrySubscriptionPaymentHandler.handleSubscriptionPaymentRetry(subject);
   }

   @PreAuthorize("@subscriptionSecurity.isOwner(#id, authentication)")
   @GetMapping("/{id}")
   public ResponseEntity<SubscriptionResponse> getById(
      @PathVariable("id")
      @Minimum final long id) {
      final SubscriptionResponse response = subscriptionGetById.getById(id);
      return ResponseEntity.ok(response);
   }

   @PreAuthorize("@subscriptionSecurity.isOwner(#email, authentication)")
   @GetMapping("/{email}/customers")
   public ResponseEntity<SubscriptionResponse> getByEmail(
      @PathVariable("email")
      @EmailValid final String email) {
      final SubscriptionResponse response = subscriptionGetByEmail.getByEmail(email);
      return ResponseEntity.ok(response);
   }

   @PreAuthorize("@subscriptionSecurity.isOwner(#customerEmail, authentication)")
   @GetMapping(params = "customerEmail")
   public ResponseEntity<Page<SubscriptionResponse>> getAllByCustomerEmail(
      @RequestParam(value = "customerEmail")
      @EmailValid final String customerEmail,
      @PageableDefault(sort = "id", direction = Sort.Direction.DESC) final Pageable pageable) {
      final PageDto<SubscriptionResponse> page = subscriptionGetAllByCustomerEmail.getAllByCustomerEmail(customerEmail, pageable);
      final Page<SubscriptionResponse> responsePage = new PageImpl<>(page.content(), pageable, page.totalElements());
      return ResponseEntity.ok(responsePage);
   }

   @PreAuthorize("@subscriptionSecurity.isOwner(#input, authentication)")
   @PostMapping
   public ResponseEntity<SubscriptionSessionResponse> create(
      @Valid
      @RequestBody
      @NotNullObject final SubscriptionRequest input) {
      final SubscriptionCheckoutResponse checkoutResponse = AsyncResolver.getResult(
         () -> stripeCheckoutService.createCheckoutSessionFrom(input), 20);

      final SubscriptionResponse subscription = subscriptionCreate.create(input);

      final PaymentRequest paymentRequest = PaymentRequest.builder()
         .sessionId(checkoutResponse.sessionId())
         .intentId(checkoutResponse.paymentIndent())
         .subscriptionSessionId(checkoutResponse.paymentSubscription())
         .isPhysical(false)
         .subscriptionId(subscription.id())
         .build();

      createPaymentUseCase.create(paymentRequest);

      final URI location = ServletUriComponentsBuilder.fromCurrentRequest()
         .path("/{id}")
         .buildAndExpand(subscription.id())
         .toUri();

      final SubscriptionSessionResponse body = new SubscriptionSessionResponse(checkoutResponse, subscription);

      return ResponseEntity.created(location).body(body);
   }

   @PreAuthorize("@subscriptionSecurity.isOwner(#id, authentication)")
   @PutMapping("/{id}")
   public ResponseEntity<SubscriptionSessionResponse> renew(
      @PathVariable("id")
      @Minimum final long id,
      @Valid
      @RequestBody
      @NotNullObject final SubscriptionRequest input) {

      final SubscriptionCheckoutResponse checkoutResponse = AsyncResolver.getResult(
         () -> stripeCheckoutService.createCheckoutSessionFrom(input), 20);

      final SubscriptionResponse subscription = subscriptionRenew.renew(id, input);

      final PaymentRequest paymentRequest = PaymentRequest.builder()
         .sessionId(checkoutResponse.sessionId())
         .intentId(checkoutResponse.paymentIndent())
         .subscriptionSessionId(checkoutResponse.paymentSubscription())
         .isPhysical(false)
         .subscriptionId(subscription.id())
         .build();

      createPaymentUseCase.create(paymentRequest);

      final SubscriptionSessionResponse body = new SubscriptionSessionResponse(checkoutResponse, subscription);

      return ResponseEntity.ok(body);
   }

   @PreAuthorize("@subscriptionSecurity.isOwner(#id, authentication)")
   @PatchMapping("/{id}")
   public ResponseEntity<SubscriptionResponse> finalize(
      @PathVariable("id")
      @Minimum final long id) {
      final SubscriptionResponse response = subscriptionFinalize.finalize(id);
      return ResponseEntity.ok(response);
   }

   @PreAuthorize("@subscriptionSecurity.isOwner(#id, authentication)")
   @DeleteMapping("/{id}")
   public ResponseEntity<Void> dropSubscription(
      @PathVariable("id")
      @Minimum final long id) {
      deleteById.softDeleteById(id);
      return ResponseEntity.noContent().build();
   }
}
