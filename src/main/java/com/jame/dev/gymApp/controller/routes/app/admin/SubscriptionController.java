package com.jame.dev.gymApp.controller.routes.app.admin;

import com.jame.dev.gymApp.controller.service.BaseControllerPatchAndPut;
import com.jame.dev.gymApp.model.dto.in.SubscriptionDtoInput;
import com.jame.dev.gymApp.model.dto.out.SubscriptionDtoOutput;
import com.jame.dev.gymApp.service.common.BaseCrudService;
import com.jame.dev.gymApp.service.common.CRUDServiceServicePatch;
import com.jame.dev.gymApp.service.common.CRUDServiceServicePut;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/v1/administration/subs")
@PreAuthorize("hasRole('ADMIN')")
public class SubscriptionController extends
        BaseControllerPatchAndPut<SubscriptionDtoOutput, SubscriptionDtoInput> {

   public SubscriptionController(
           final BaseCrudService<SubscriptionDtoOutput, SubscriptionDtoInput, Long> service,
           final CRUDServiceServicePatch<SubscriptionDtoOutput, SubscriptionDtoInput, Long> patchService,
           final CRUDServiceServicePut<SubscriptionDtoOutput, SubscriptionDtoInput, Long> putService) {
      super(service, SubscriptionDtoOutput::id, patchService, putService);
   }

   @GetMapping
   public ResponseEntity<@NonNull Page<@NonNull SubscriptionDtoOutput>> getSubscriptionPage(
           @RequestParam("page") final int page, @RequestParam("size") final int size) {
      return super.getPage(page, size);
   }

   @GetMapping("/{id}")
   public ResponseEntity<@NonNull SubscriptionDtoOutput> getSubscription(
           @PathVariable("id") final long id) {
      return super.getOne(id);
   }

   @PostMapping
   public ResponseEntity<@NonNull SubscriptionDtoOutput> postSubscription(
           @RequestBody final SubscriptionDtoInput subscriptionDtoInput) {
      final String location = "/admin/subs";
      return super.create(subscriptionDtoInput, location);
   }

   @PutMapping("/{id}")
   public ResponseEntity<@NonNull SubscriptionDtoOutput> updateSubscription(
           @PathVariable("id") final long id, @RequestBody final SubscriptionDtoInput subscriptionDtoInput) {
      return super.update(id, subscriptionDtoInput);
   }


   @PutMapping("/{id}/renew")
   public ResponseEntity<@NonNull SubscriptionDtoOutput> renewSubscription(
           @PathVariable("id") final long id, @RequestBody final SubscriptionDtoInput subscriptionDtoInput) {
      return super.put(id, subscriptionDtoInput);
   }

   @PatchMapping("/{id}")
   public ResponseEntity<@NonNull SubscriptionDtoOutput> finalizeSubscription(
           @PathVariable("id") final long id) {
      return super.patch(id);
   }

   @DeleteMapping("/{id}")
   public ResponseEntity<Void> deleteSubscription(
           @PathVariable("id") final long id) {
      return super.delete(id);
   }
}

