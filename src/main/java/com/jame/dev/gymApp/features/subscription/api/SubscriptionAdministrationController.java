package com.jame.dev.gymApp.features.subscription.api;

import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionRequest;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.*;
import com.jame.dev.gymApp.features.subscription.application.usecases.query.GetByIdSubscriptionUseCase;
import com.jame.dev.gymApp.features.subscription.application.usecases.query.GetPageSubscriptionUseCase;
import com.jame.dev.gymApp.features.subscription.infrastructure.notification.service.SubscriptionNotificationAppService;
import com.jame.dev.gymApp.infrastructure.annotation.Minimum;
import com.jame.dev.gymApp.infrastructure.annotation.NotNullObject;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/app/v1/administration/subs")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Validated
public class SubscriptionAdministrationController {

   private final GetPageSubscriptionUseCase subscriptionGetPage;
   private final GetByIdSubscriptionUseCase subscriptionGetById;
   private final CreateSubscriptionUseCase subscriptionCreate;
   private final UpdateSubscriptionUseCase subscriptionUpdate;
   private final RenewSubscriptionUseCase subscriptionRenew;
   private final FinalizeSubscriptionUseCase subscriptionFinalize;
   private final SoftDeleteSubscriptionByIdUseCase subscriptionSoftDelete;
   private final SubscriptionNotificationAppService subsNotificationAppService;

   @GetMapping
   public ResponseEntity<Page<SubscriptionResponse>> getPage(
      @PageableDefault(
         sort = "id",
         direction = Sort.Direction.DESC) final Pageable pageable,
      @RequestParam(required = false, name = "search") final String search) {
      final PageDto<SubscriptionResponse> pageDto = subscriptionGetPage.getPage(pageable, search);
      final Page<SubscriptionResponse> page = new PageImpl<>(pageDto.content(), pageable, pageDto.totalElements());
      return ResponseEntity.ok(page);
   }

   @GetMapping("/{id}")
   public ResponseEntity<SubscriptionResponse> getById(
           @PathVariable("id") @Minimum final long id) {
      final SubscriptionResponse response = subscriptionGetById.getById(id);
      return ResponseEntity.ok(response);
   }

   @PostMapping
   public ResponseEntity<SubscriptionResponse> create(
           @RequestBody @Valid @NotNullObject final SubscriptionRequest subscriptionRequest) {
      final SubscriptionResponse response = subscriptionCreate.create(subscriptionRequest);
      final URI location = ServletUriComponentsBuilder.fromCurrentRequest()
              .path("/{id}")
              .buildAndExpand(response.id())
              .toUri();
      return ResponseEntity.created(location).body(response);
   }

   @PostMapping("/notify")
   public ResponseEntity<Void> notifySubscribers() {
      subsNotificationAppService.notifySubscriptionEnds();
      return ResponseEntity.ok().build();
   }

   @PutMapping("/{id}")
   public ResponseEntity<SubscriptionResponse> update(
           @PathVariable("id")
           @Minimum final long id,
           @RequestBody
           @Valid
           @NotNullObject final SubscriptionRequest subscriptionRequest) {
      final SubscriptionResponse response = subscriptionUpdate.update(id, subscriptionRequest);
      return ResponseEntity.ok(response);
   }

   @PutMapping("/{id}/renew")
   public ResponseEntity<SubscriptionResponse> renew(
           @PathVariable("id")
           @Minimum final long id,
           @RequestBody
           @Valid
           @NotNullObject final SubscriptionRequest subscriptionRequest) {
      final SubscriptionResponse response = subscriptionRenew.renew(id, subscriptionRequest);
      return ResponseEntity.ok(response);
   }

   @PatchMapping("/{id}")
   public ResponseEntity<SubscriptionResponse> finalize(
           @PathVariable("id")
           @Minimum final long id) {
      final SubscriptionResponse response = subscriptionFinalize.finalize(id);
      return ResponseEntity.ok(response);
   }

   @DeleteMapping("/{id}")
   public ResponseEntity<Void> delete(
           @PathVariable("id")
           @Minimum final long id) {
      subscriptionSoftDelete.softDeleteById(id);
      return ResponseEntity.noContent().build();
   }
}

