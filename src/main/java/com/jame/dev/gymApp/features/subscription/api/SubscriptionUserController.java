package com.jame.dev.gymApp.features.subscription.api;

import com.jame.dev.gymApp.infrastructure.annotation.EmailValid;
import com.jame.dev.gymApp.infrastructure.annotation.Minimum;
import com.jame.dev.gymApp.infrastructure.annotation.NotNullObject;
import com.jame.dev.gymApp.infrastructure.web.FullControllerIdentifiable;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.application.support.mapper.BaseMapper;
import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionRequest;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.application.contract.EmailIdentifiable;
import com.jame.dev.gymApp.application.contract.FullService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/v1/subscriptions")
@PreAuthorize("hasRole('USER')")
@Validated
public class SubscriptionUserController extends FullControllerIdentifiable<
        SubscriptionEntity, SubscriptionResponse, SubscriptionRequest> {

   protected SubscriptionUserController(
      FullService<SubscriptionResponse, SubscriptionRequest> service,
      EmailIdentifiable<SubscriptionEntity> identifiable,
      BaseMapper<SubscriptionEntity, SubscriptionResponse> mapper) {
      super(service, SubscriptionResponse::id, identifiable, mapper);
   }

   @PreAuthorize("@subscriptionSecurity.isOwner(#id, authentication)")
   @GetMapping("/{id}")
   public ResponseEntity<SubscriptionResponse> getSub(
           @PathVariable("id")
           @Minimum
           final long id) {
      return super.getOne(id);
   }

   @PreAuthorize("@subscriptionSecurity.isOwner(#email, authentication)")
   @GetMapping("/customer/{email}")
   public ResponseEntity<SubscriptionResponse> getSubByEmail(
           @PathVariable("email")
           @EmailValid
           final String email) {
      return super.getByEmail(email);
   }

   @PreAuthorize("@subscriptionSecurity.isOwner(#input, authentication)")
   @PostMapping
   public ResponseEntity<SubscriptionResponse> subscribe(
           @Valid
           @RequestBody
           @NotNullObject final SubscriptionRequest input) {
      return super.create(input);
   }

   @PreAuthorize("@subscriptionSecurity.isOwner(#id, authentication)")
   @PutMapping("/{id}")
   public ResponseEntity<SubscriptionResponse> renew(
           @PathVariable("id")
           @Minimum final long id,
           @Valid
           @RequestBody
           @NotNullObject final SubscriptionRequest input
   ) {
      return super.put(id, input);
   }

   @PreAuthorize("@subscriptionSecurity.isOwner(#id, authentication)")
   @PatchMapping("/{id}")
   public ResponseEntity<SubscriptionResponse> finalizeSubscription(
           @PathVariable("id")
           @Minimum final long id) {
      return super.patch(id);
   }
}
