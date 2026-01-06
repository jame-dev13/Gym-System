package com.jame.dev.gymApp.controller.routes.app.admin;

import com.jame.dev.gymApp.cache.service.AppCacheService;
import com.jame.dev.gymApp.controller.service.BaseControllerPatchable;
import com.jame.dev.gymApp.entity.SubscriptionEntity;
import com.jame.dev.gymApp.mapper.BaseMapper;
import com.jame.dev.gymApp.model.dto.in.SubscriptionDtoInput;
import com.jame.dev.gymApp.model.dto.out.SubscriptionDtoOutput;
import com.jame.dev.gymApp.service.common.BaseCrudService;
import com.jame.dev.gymApp.service.common.CRUDServiceServicePatch;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/v1/administration/subscriptions")
@PreAuthorize("hasRole('ADMIN')")
public class SubscriptionController extends BaseControllerPatchable<SubscriptionEntity, SubscriptionDtoInput, SubscriptionDtoOutput> {

   protected SubscriptionController(
           final BaseCrudService<SubscriptionEntity, SubscriptionDtoInput, Long> service,
           final AppCacheService<SubscriptionDtoOutput> cache,
           final BaseMapper<SubscriptionEntity, SubscriptionDtoOutput> mapper,
           final CRUDServiceServicePatch<SubscriptionEntity, SubscriptionDtoInput, Long> patchService) {
      super(service, cache, mapper, "subscriptions", SubscriptionEntity::getId, patchService);
   }

   @GetMapping
   public ResponseEntity<@NonNull Page<@NonNull SubscriptionDtoOutput>> getSubscriptionPage(@RequestParam("page") final int page,
                                                                                            @RequestParam("size") final int size){
      return super.getPage(page, size);
   }

   @GetMapping("/{id}")
   public ResponseEntity<@NonNull SubscriptionDtoOutput> getSubscription(@PathVariable("id") final long id){
      return super.getOne(id);
   }

   @PostMapping
   public ResponseEntity<@NonNull SubscriptionDtoOutput> postSubscription(@RequestBody final SubscriptionDtoInput subscriptionDtoInput){
      final String location = "/admin/subscriptions";
      return super.create(subscriptionDtoInput, location);
   }

   @PatchMapping("/{id}")
   public ResponseEntity<@NonNull SubscriptionDtoOutput> finalizeSubscription(@PathVariable("id") final long id){
      return super.patch(id);
   }

   @SuppressWarnings("NullableProblems")
   @DeleteMapping("/{id}")
   public ResponseEntity<Void> deleteSubscription(@PathVariable("id") final long id){
      return super.delete(id);
   }
}

