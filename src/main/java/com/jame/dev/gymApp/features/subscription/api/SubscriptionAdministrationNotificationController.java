package com.jame.dev.gymApp.features.subscription.api;

import com.jame.dev.gymApp.features.subscription.api.response.NotificationAvailabilityResponse;
import com.jame.dev.gymApp.features.subscription.infrastructure.notification.usecases.CheckNotificationAvailabilityUseCase;
import com.jame.dev.gymApp.features.subscription.infrastructure.notification.usecases.NotifyAllSubscriptionsUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app/v1/administration/subs/notifications")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Validated
@Slf4j
public class SubscriptionAdministrationNotificationController {
   private final CheckNotificationAvailabilityUseCase checkNotificationAvailabilityUseCase;
   private final NotifyAllSubscriptionsUseCase notifyAllSubscriptionsUseCase;

   @GetMapping("/availability")
   public ResponseEntity<NotificationAvailabilityResponse> getNotificationAvailability() {
      final long start = System.nanoTime();

      log.info("AVAILABILITY CONTROLLER -> START");

      final var response =
         checkNotificationAvailabilityUseCase.checkAvailability();

      log.info(
         "AVAILABILITY CONTROLLER -> END: {} ms",
         (System.nanoTime() - start) / 1_000_000
      );

      return ResponseEntity.ok(response);
   }

   @PostMapping
   public ResponseEntity<Void> processNotifyRequest() {
      final long start = System.nanoTime();

      log.info("POST -> START");

      notifyAllSubscriptionsUseCase.notifySubscriptions();

      log.info(
         "POST -> END: {} ms",
         (System.nanoTime() - start) / 1_000_000
      );

      return ResponseEntity.accepted().build();
   }
}
