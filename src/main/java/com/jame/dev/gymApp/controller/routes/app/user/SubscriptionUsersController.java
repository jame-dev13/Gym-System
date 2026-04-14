package com.jame.dev.gymApp.controller.routes.app.user;

import com.jame.dev.gymApp.aspects.annotations.constraints.EmailValid;
import com.jame.dev.gymApp.aspects.annotations.constraints.Minimum;
import com.jame.dev.gymApp.aspects.annotations.constraints.NotNullObject;
import com.jame.dev.gymApp.controller.service.FullControllerIdentifiable;
import com.jame.dev.gymApp.entity.SubscriptionEntity;
import com.jame.dev.gymApp.mapper.BaseMapper;
import com.jame.dev.gymApp.model.dto.in.SubscriptionDtoInput;
import com.jame.dev.gymApp.model.dto.out.SubscriptionDtoOutput;
import com.jame.dev.gymApp.service.common.EmailIdentifiable;
import com.jame.dev.gymApp.service.common.FullService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/v1/subscriptions")
@PreAuthorize("hasRole('USER')")
@Validated
public class SubscriptionUsersController extends FullControllerIdentifiable<
        SubscriptionEntity, SubscriptionDtoOutput, SubscriptionDtoInput> {

   protected SubscriptionUsersController(
      FullService<SubscriptionDtoOutput, SubscriptionDtoInput> service,
      EmailIdentifiable<SubscriptionEntity> identifiable,
      BaseMapper<SubscriptionEntity, SubscriptionDtoOutput> mapper) {
      super(service, SubscriptionDtoOutput::id, identifiable, mapper);
   }

   @PreAuthorize("@subscriptionSecurity.isOwner(#id, authentication)")
   @GetMapping("/{id}")
   public ResponseEntity<SubscriptionDtoOutput> getSub(
           @PathVariable("id")
           @Minimum
           final long id) {
      return super.getOne(id);
   }

   @PreAuthorize("@subscriptionSecurity.isOwner(#email, authentication)")
   @GetMapping("/customer/{email}")
   public ResponseEntity<SubscriptionDtoOutput> getSubByEmail(
           @PathVariable("email")
           @EmailValid
           final String email) {
      return super.getByEmail(email);
   }

   @PreAuthorize("@subscriptionSecurity.isOwner(#input, authentication)")
   @PostMapping
   public ResponseEntity<SubscriptionDtoOutput> subscribe(
           @Valid
           @RequestBody
           @NotNullObject final SubscriptionDtoInput input) {
      return super.create(input);
   }

   @PreAuthorize("@subscriptionSecurity.isOwner(#id, authentication)")
   @PutMapping("/{id}")
   public ResponseEntity<SubscriptionDtoOutput> renew(
           @PathVariable("id")
           @Minimum final long id,
           @Valid
           @RequestBody
           @NotNullObject final SubscriptionDtoInput input
   ) {
      return super.put(id, input);
   }

   @PreAuthorize("@subscriptionSecurity.isOwner(#id, authentication)")
   @PatchMapping("/{id}")
   public ResponseEntity<SubscriptionDtoOutput> finalizeSubscription(
           @PathVariable("id")
           @Minimum final long id) {
      return super.patch(id);
   }
}
