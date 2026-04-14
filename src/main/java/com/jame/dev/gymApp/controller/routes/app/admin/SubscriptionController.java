package com.jame.dev.gymApp.controller.routes.app.admin;

import com.jame.dev.gymApp.aspects.annotations.constraints.Minimum;
import com.jame.dev.gymApp.aspects.annotations.constraints.NotNullObject;
import com.jame.dev.gymApp.controller.service.FullController;
import com.jame.dev.gymApp.model.dto.in.SubscriptionDtoInput;
import com.jame.dev.gymApp.model.dto.out.SubscriptionDtoOutput;
import com.jame.dev.gymApp.service.in.SubscriptionService;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/v1/administration/subs")
@PreAuthorize("hasRole('ADMIN')")
public class SubscriptionController extends
   FullController<SubscriptionDtoOutput, SubscriptionDtoInput> {

   public SubscriptionController(final SubscriptionService service) {
      super(service, SubscriptionDtoOutput::id);
   }

   @GetMapping
   public ResponseEntity<@NonNull Page<@NonNull SubscriptionDtoOutput>> getSubscriptionPage(
           @RequestParam("page") final int page,
           @RequestParam("size") final int size) {
      return super.getPage(page, size);
   }

   @GetMapping("/{id}")
   public ResponseEntity<@NonNull SubscriptionDtoOutput> getSubscription(
           @PathVariable("id") @Minimum final long id) {
      return super.getOne(id);
   }

   @PostMapping
   public ResponseEntity<@NonNull SubscriptionDtoOutput> postSubscription(
           @RequestBody @Valid @NotNullObject final SubscriptionDtoInput subscriptionDtoInput) {
      return super.create(subscriptionDtoInput);
   }

   @PutMapping("/{id}")
   public ResponseEntity<@NonNull SubscriptionDtoOutput> updateSubscription(
           @PathVariable("id")
           @Minimum final long id,
           @RequestBody
           @Valid
           @NotNullObject final SubscriptionDtoInput subscriptionDtoInput) {
      return super.update(id, subscriptionDtoInput);
   }


   @PutMapping("/{id}/renew")
   public ResponseEntity<@NonNull SubscriptionDtoOutput> renewSubscription(
           @PathVariable("id")
           @Minimum final long id,
           @RequestBody
           @Valid
           @NotNullObject final SubscriptionDtoInput subscriptionDtoInput) {
      return super.put(id, subscriptionDtoInput);
   }

   @PatchMapping("/{id}")
   public ResponseEntity<@NonNull SubscriptionDtoOutput> finalizeSubscription(
           @PathVariable("id")
           @Minimum final long id) {
      return super.patch(id);
   }

   @DeleteMapping("/{id}")
   public ResponseEntity<Void> deleteSubscription(
           @PathVariable("id")
           @Minimum final long id) {
      return super.delete(id);
   }
}

