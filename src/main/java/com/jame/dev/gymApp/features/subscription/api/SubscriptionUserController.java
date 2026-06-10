package com.jame.dev.gymApp.features.subscription.api;

import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionRequest;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.CreateSubscriptionUseCase;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.FinalizeSubscriptionUseCase;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.RenewSubscriptionUseCase;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.UpdateSubscriptionUseCase;
import com.jame.dev.gymApp.features.subscription.application.usecases.query.GetByEmailSubscriptionUseCase;
import com.jame.dev.gymApp.features.subscription.application.usecases.query.GetByIdSubscriptionUseCase;
import com.jame.dev.gymApp.infrastructure.annotation.EmailValid;
import com.jame.dev.gymApp.infrastructure.annotation.Minimum;
import com.jame.dev.gymApp.infrastructure.annotation.NotNullObject;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
   private final CreateSubscriptionUseCase subscriptionCreate;
   private final UpdateSubscriptionUseCase subscriptionUpdate;
   private final RenewSubscriptionUseCase subscriptionRenew;
   private final FinalizeSubscriptionUseCase subscriptionFinalize;

   @PreAuthorize("@subscriptionSecurity.isOwner(#id, authentication)")
   @GetMapping("/{id}")
   public ResponseEntity<SubscriptionResponse> getById(
           @PathVariable("id")
           @Minimum
           final long id) {
      final SubscriptionResponse response = subscriptionGetById.getById(id);
      return ResponseEntity.ok(response);
   }

   @PreAuthorize("@subscriptionSecurity.isOwner(#email, authentication)")
   @GetMapping("/{email}/customers")
   public ResponseEntity<SubscriptionResponse> getByEmail(
           @PathVariable("email")
           @EmailValid
           final String email) {
      final SubscriptionResponse response = subscriptionGetByEmail.getByEmail(email);
      return ResponseEntity.ok(response);
   }

   @PreAuthorize("@subscriptionSecurity.isOwner(#input, authentication)")
   @PostMapping
   public ResponseEntity<SubscriptionResponse> create(
           @Valid
           @RequestBody
           @NotNullObject final SubscriptionRequest input) {
      final SubscriptionResponse response = subscriptionCreate.create(input);
      final URI location = ServletUriComponentsBuilder.fromCurrentRequest()
              .path("/{id}")
              .buildAndExpand(response.id())
              .toUri();
      return ResponseEntity.created(location).body(response);
   }

   @PreAuthorize("@subscriptionSecurity.isOwner(#id, authentication)")
   @PutMapping("/{id}")
   public ResponseEntity<SubscriptionResponse> renew(
           @PathVariable("id")
           @Minimum final long id,
           @Valid
           @RequestBody
           @NotNullObject final SubscriptionRequest input) {
      final SubscriptionResponse response = subscriptionRenew.renew(id, input);
      return ResponseEntity.ok(response);
   }

   @PreAuthorize("@subscriptionSecurity.isOwner(#id, authentication)")
   @PatchMapping("/{id}")
   public ResponseEntity<SubscriptionResponse> finalize(
           @PathVariable("id")
           @Minimum final long id) {
      final SubscriptionResponse response = subscriptionFinalize.finalize(id);
      return ResponseEntity.ok(response);
   }
}
