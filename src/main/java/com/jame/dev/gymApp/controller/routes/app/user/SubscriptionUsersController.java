package com.jame.dev.gymApp.controller.routes.app.user;

import com.jame.dev.gymApp.cache.service.AppCacheService;
import com.jame.dev.gymApp.controller.service.ControllerPatchPut;
import com.jame.dev.gymApp.entity.SubscriptionEntity;
import com.jame.dev.gymApp.mapper.BaseMapper;
import com.jame.dev.gymApp.model.dto.in.SubscriptionDtoInput;
import com.jame.dev.gymApp.model.dto.out.SubscriptionDtoOutput;
import com.jame.dev.gymApp.service.common.BaseCrudService;
import com.jame.dev.gymApp.service.common.CRUDServiceServicePatch;
import com.jame.dev.gymApp.service.common.CRUDServiceServicePut;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/v1/subscriptions")
@PreAuthorize("hasRole('USER')")
public class SubscriptionUsersController extends ControllerPatchPut<SubscriptionEntity, SubscriptionDtoInput, SubscriptionDtoOutput> {
   public SubscriptionUsersController(BaseCrudService<SubscriptionEntity, SubscriptionDtoInput, Long> service,
                                      AppCacheService<SubscriptionDtoOutput> cache, BaseMapper<SubscriptionEntity, SubscriptionDtoOutput> mapper,
                                      CRUDServiceServicePatch<SubscriptionEntity, SubscriptionDtoInput, Long> patchService, CRUDServiceServicePut<SubscriptionEntity, SubscriptionDtoInput, Long> putService) {
      super(service, cache, mapper, "subscriptions", SubscriptionEntity::getId, patchService, putService);
   }

   @PreAuthorize("@ownerSecurity.isOwner(#id, authentication)")
   @GetMapping("/{id}")
   public ResponseEntity<SubscriptionDtoOutput> getSub(@PathVariable("id") final Long id) {
      return super.getOne(id);
   }

   @PostMapping
   public ResponseEntity<SubscriptionDtoOutput> subscribe(@RequestBody final SubscriptionDtoInput input) {
      return super.create(input, "/gym-app/v1/subcriptions");
   }

   @PreAuthorize("@ownerSecurity.isOwner(#id, authentication) and @authorize.checkIdentity(#input)")
   @PutMapping("/{id}")
   public ResponseEntity<SubscriptionDtoOutput> renew(
           @PathVariable("id") final Long id, @RequestBody final SubscriptionDtoInput input
   ) {
      return super.put(id, input);
   }

   @PreAuthorize("@ownerSecurity.isOwner(#id, authentication)")
   @PatchMapping("/{id}")
   public ResponseEntity<SubscriptionDtoOutput> finalizeSubscription(@PathVariable("id") final Long id) {
      return super.patch(id);
   }
}
