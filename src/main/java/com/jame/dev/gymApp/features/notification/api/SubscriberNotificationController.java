package com.jame.dev.gymApp.features.notification.api;

import com.jame.dev.gymApp.features.auth.domain.model.AuthPrincipal;
import com.jame.dev.gymApp.features.notification.api.request.DayRangeRequest;
import com.jame.dev.gymApp.features.notification.api.response.SubscriberNotificationResponse;
import com.jame.dev.gymApp.features.notification.application.usecases.mutation.ActivateSubscriberNotificationUseCase;
import com.jame.dev.gymApp.features.notification.application.usecases.mutation.ChangeDayRangeSubscriberNotificationUseCase;
import com.jame.dev.gymApp.features.notification.application.usecases.mutation.DeactivateSubscriberNotificationUseCase;
import com.jame.dev.gymApp.features.notification.application.usecases.query.GetCurrentSubscriberNotificationUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/app/v1/notifications/current")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
@Validated
public class SubscriberNotificationController {
   private final GetCurrentSubscriberNotificationUseCase getCurrentSubscriberNotificationUseCase;
   private final ActivateSubscriberNotificationUseCase activateSubscriberNotificationUseCase;
   private final DeactivateSubscriberNotificationUseCase deactivateSubscriberNotificationUseCase;
   private final ChangeDayRangeSubscriberNotificationUseCase changeDayRangeSubscriberNotificationUseCase;

   @GetMapping
   public ResponseEntity<SubscriberNotificationResponse> getCurrent(@AuthenticationPrincipal AuthPrincipal principal) {
      return ResponseEntity.ok(getCurrentSubscriberNotificationUseCase.getCurrent(principal));
   }

   @PatchMapping("/activate")
   public ResponseEntity<SubscriberNotificationResponse> activateNotification(
      @AuthenticationPrincipal AuthPrincipal principal
   ) {
      return ResponseEntity.ok(activateSubscriberNotificationUseCase.activateNotification(principal));
   }

   @PatchMapping("/deactivate")
   public ResponseEntity<SubscriberNotificationResponse> deactivateNotification(
      @AuthenticationPrincipal AuthPrincipal principal
   ) {
      return ResponseEntity.ok(deactivateSubscriberNotificationUseCase.deactivateNotification(principal));
   }

   @PatchMapping("/range")
   public ResponseEntity<SubscriberNotificationResponse> changeDayRange(
      @AuthenticationPrincipal AuthPrincipal principal,
      @Valid @RequestBody DayRangeRequest request
   ) {
      return ResponseEntity.ok(changeDayRangeSubscriberNotificationUseCase.changeDayRange(principal, request));
   }
}
