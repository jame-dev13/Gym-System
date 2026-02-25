package com.jame.dev.gymApp.controller.routes.app.user;

import com.jame.dev.gymApp.controller.service.ControllerPutPatchIdentifiable;
import com.jame.dev.gymApp.entity.SubscriptionEntity;
import com.jame.dev.gymApp.mapper.BaseMapper;
import com.jame.dev.gymApp.model.dto.in.SubscriptionDtoInput;
import com.jame.dev.gymApp.model.dto.out.SubscriptionDtoOutput;
import com.jame.dev.gymApp.service.common.BaseCrudService;
import com.jame.dev.gymApp.service.common.CRUDServiceServicePatch;
import com.jame.dev.gymApp.service.common.CRUDServiceServicePut;
import com.jame.dev.gymApp.service.common.EmailIdentifiable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/v1/subscriptions")
@PreAuthorize("hasRole('USER')")
public class SubscriptionUsersBaseController extends ControllerPutPatchIdentifiable<
        SubscriptionEntity, SubscriptionDtoOutput, SubscriptionDtoInput> {

   public SubscriptionUsersBaseController(
           BaseCrudService<SubscriptionDtoOutput, SubscriptionDtoInput, Long> service,
           CRUDServiceServicePatch<SubscriptionDtoOutput, SubscriptionDtoInput, Long> patchService,
           CRUDServiceServicePut<SubscriptionDtoOutput, SubscriptionDtoInput, Long> putService,
           EmailIdentifiable<SubscriptionEntity> identifiable,
           BaseMapper<SubscriptionEntity, SubscriptionDtoOutput> mapper) {
      super(service, SubscriptionDtoOutput::id, patchService, putService, identifiable, mapper);
   }

   @PreAuthorize("@subscriptionSecurity.isOwner(#id, authentication)")
   @GetMapping("/{id}")
   public ResponseEntity<SubscriptionDtoOutput> getSub(@PathVariable("id") final Long id) {
      return super.getOne(id);
   }

   @PreAuthorize("@subscriptionSecurity.isOwner(#email, authentication)")
   @GetMapping("/customer/{email}")
   public ResponseEntity<SubscriptionDtoOutput> getSubByEmail(@PathVariable("email") final String email) {
      return super.getByEmail(email);
   }

   @PostMapping
   public ResponseEntity<SubscriptionDtoOutput> subscribe(@RequestBody final SubscriptionDtoInput input) {
      return super.create(input, "/app/v1/subcriptions");
   }

   @PreAuthorize("@subscriptionSecurity.isOwner(#id, authentication) and @authorize.checkIdentity(#input)")
   @PutMapping("/{id}")
   public ResponseEntity<SubscriptionDtoOutput> renew(
           @PathVariable("id") final Long id, @RequestBody final SubscriptionDtoInput input
   ) {
      return super.put(id, input);
   }

   @PreAuthorize("@subscriptionSecurity.isOwner(#id, authentication)")
   @PatchMapping("/{id}")
   public ResponseEntity<SubscriptionDtoOutput> finalizeSubscription(@PathVariable("id") final Long id) {
      return super.patch(id);
   }
}
