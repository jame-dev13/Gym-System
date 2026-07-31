package com.jame.dev.gymApp.features.notification.api;

import com.jame.dev.gymApp.features.notification.api.request.SubscriberNotificationUpdateRequest;
import com.jame.dev.gymApp.features.notification.api.response.SubscriberNotificationResponse;
import com.jame.dev.gymApp.features.notification.application.usecases.mutation.DeleteSubscriberNotificationById;
import com.jame.dev.gymApp.features.notification.application.usecases.mutation.UpdateSubscriberNotificationUseCase;
import com.jame.dev.gymApp.features.notification.application.usecases.query.GetByIdSubscriberNotificationUseCase;
import com.jame.dev.gymApp.infrastructure.annotation.NotNullObject;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/app/v1/notifications")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
@Validated
public class SubscriberNotificationController {
   private final GetByIdSubscriberNotificationUseCase getById;
   private final UpdateSubscriberNotificationUseCase update;
   private final DeleteSubscriberNotificationById delete;

   @GetMapping("/{id}")
   public ResponseEntity<SubscriberNotificationResponse> getById(@PathVariable final UUID id) {
      return ResponseEntity.ok(getById.getById(id));
   }

   @PatchMapping("/{id}")
   public ResponseEntity<SubscriberNotificationResponse> update(
      @PathVariable final UUID id,
      @RequestBody @Valid @NotNullObject final SubscriberNotificationUpdateRequest request) {
      return ResponseEntity.ok(update.updateSubscriberNotification(id, request));
   }

   @DeleteMapping("/{id}")
   public ResponseEntity<Void> delete(@PathVariable final UUID id) {
      delete.deleteSubscriberNotificationById(id);
      return ResponseEntity.noContent().build();
   }
}
