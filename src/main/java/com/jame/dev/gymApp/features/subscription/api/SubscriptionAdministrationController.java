package com.jame.dev.gymApp.features.subscription.api;

import com.jame.dev.gymApp.features.subscription.infrastructure.notification.service.SubscriptionNotificationAppService;
import com.jame.dev.gymApp.infrastructure.annotation.Minimum;
import com.jame.dev.gymApp.infrastructure.annotation.NotNullObject;
import com.jame.dev.gymApp.infrastructure.web.FullController;
import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionRequest;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.features.subscription.application.contract.SubscriptionService;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/v1/administration/subs")
@PreAuthorize("hasRole('ADMIN')")
public class SubscriptionAdministrationController extends
   FullController<SubscriptionResponse, SubscriptionRequest> {

   private final SubscriptionNotificationAppService subsNotificationAppService;

   public SubscriptionAdministrationController(
      final SubscriptionService service,
      final SubscriptionNotificationAppService subsNotificationAppService) {
      super(service, SubscriptionResponse::id);
      this.subsNotificationAppService = subsNotificationAppService;
   }

   @GetMapping
   public ResponseEntity<@NonNull Page<@NonNull SubscriptionResponse>> getSubscriptionPage(
      @PageableDefault(
         sort = "id",
         direction = Sort.Direction.DESC) final Pageable pageable,
      @RequestParam(required = false, name = "search") String search) {
      return super.getPage(pageable, search);
   }

   @GetMapping("/{id}")
   public ResponseEntity<@NonNull SubscriptionResponse> getSubscription(
           @PathVariable("id") @Minimum final long id) {
      return super.getOne(id);
   }

   @PostMapping
   public ResponseEntity<@NonNull SubscriptionResponse> postSubscription(
           @RequestBody @Valid @NotNullObject final SubscriptionRequest subscriptionRequest) {
      return super.create(subscriptionRequest);
   }

   @PostMapping("/notify")
   public ResponseEntity<Void> notifySubscribers() {
      subsNotificationAppService.notifySubscriptionEnds();
      return ResponseEntity.ok().build();
   }

   @PutMapping("/{id}")
   public ResponseEntity<@NonNull SubscriptionResponse> updateSubscription(
           @PathVariable("id")
           @Minimum final long id,
           @RequestBody
           @Valid
           @NotNullObject final SubscriptionRequest subscriptionRequest) {
      return super.update(id, subscriptionRequest);
   }


   @PutMapping("/{id}/renew")
   public ResponseEntity<@NonNull SubscriptionResponse> renewSubscription(
           @PathVariable("id")
           @Minimum final long id,
           @RequestBody
           @Valid
           @NotNullObject final SubscriptionRequest subscriptionRequest) {
      return super.put(id, subscriptionRequest);
   }

   @PatchMapping("/{id}")
   public ResponseEntity<@NonNull SubscriptionResponse> finalizeSubscription(
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

