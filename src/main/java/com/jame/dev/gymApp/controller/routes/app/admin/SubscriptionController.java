package com.jame.dev.gymApp.controller.routes.app.admin;

import com.jame.dev.gymApp.cache.service.AppCacheService;
import com.jame.dev.gymApp.controller.service.BaseController;
import com.jame.dev.gymApp.entity.SubscriptionEntity;
import com.jame.dev.gymApp.mapper.BaseMapper;
import com.jame.dev.gymApp.model.dto.in.SubscriptionDtoInput;
import com.jame.dev.gymApp.model.dto.out.SubscriptionDtoOutput;
import com.jame.dev.gymApp.service.common.CRUDService;
import com.jame.dev.gymApp.service.in.SubscriptionService;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/subscriptions")
public class SubscriptionController extends BaseController<SubscriptionEntity, SubscriptionDtoInput, SubscriptionDtoOutput> {

   private final SubscriptionService subscriptionService;
   protected SubscriptionController(final CRUDService<SubscriptionEntity, SubscriptionDtoInput> service,
                                    final BaseMapper<SubscriptionEntity, SubscriptionDtoOutput> mapper,
                                    final AppCacheService<SubscriptionDtoOutput> cache,
                                    final SubscriptionService subscriptionService) {
      super(service, mapper, cache, "subscriptions");
      this.subscriptionService = subscriptionService;
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
      final SubscriptionEntity subscription = subscriptionService.finalizeSubscription(id);
      return super.ok(subscription);
   }

   @DeleteMapping("/{id}")
   public ResponseEntity<Void> deleteSubscription(@PathVariable("id") final long id){
      return super.delete(id);
   }
}
