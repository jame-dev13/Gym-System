package com.jame.dev.gymApp.notifications.usecases.mutation;

import com.jame.dev.gymApp.domain.exception.NotFoundException;
import com.jame.dev.gymApp.features.auth.domain.model.AuthPrincipal;
import com.jame.dev.gymApp.features.auth.domain.model.UserPrincipal;
import com.jame.dev.gymApp.features.notification.api.response.SubscriberNotificationResponse;
import com.jame.dev.gymApp.features.notification.application.contract.SubscriberNotificationFactory;
import com.jame.dev.gymApp.features.notification.application.service.mutation.ActivateSubscriberNotificationUseCaseService;
import com.jame.dev.gymApp.features.notification.domain.model.SubscriberNotificationEntity;
import com.jame.dev.gymApp.features.notification.domain.repository.SubscriberNotificationMutationRepository;
import com.jame.dev.gymApp.features.notification.infrastructure.query.SubscriberNotifiableRetrieverRepository;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class ActivateSubscriberNotificationUseCaseServiceTest {

   @Mock
   private SubscriberNotifiableRetrieverRepository retrieverRepository;

   @Mock
   private SubscriberNotificationMutationRepository mutationRepository;

   @Mock
   private SubscriberNotificationFactory factory;

   @InjectMocks
   private ActivateSubscriberNotificationUseCaseService service;

   private final AuthPrincipal principal = UserPrincipal.builder()
      .id(1L)
      .username("user@mail.com")
      .password("encoded")
      .authorities(Collections.emptyList())
      .build();

   @Test
   @DisplayName("Should activate the notification, persist it and return the mapped response when it was inactive")
   void activateNotification_whenInactive_activatesAndSaves() {
      final var entity = SubscriberNotificationEntity.builder()
         .subscription(mock(SubscriptionEntity.class))
         .rangeNotificationDays(5)
         .nextNotificationDate(LocalDateTime.of(2026, 9, 1, 12, 0))
         .notifiable(false)
         .build();
      final var expected = new SubscriberNotificationResponse(5, "2026-09-01T12:00", true);

      given(retrieverRepository.findByCurrentUsername(principal.username())).willReturn(Optional.of(entity));
      given(mutationRepository.save(entity)).willReturn(entity);
      given(factory.createFromEntity(entity)).willReturn(expected);

      final var result = service.activateNotification(principal);

      assertAll(
         () -> assertNotNull(result, "Result should not be null."),
         () -> assertSame(expected, result, "Result should be the factory built response."),
         () -> assertTrue(entity.isNotifiable(), "Entity should be flagged as notifiable before saving.")
      );

      verify(retrieverRepository).findByCurrentUsername("user@mail.com");
      verify(mutationRepository).save(entity);
      verify(factory).createFromEntity(entity);
      verifyNoMoreInteractions(retrieverRepository, mutationRepository, factory);
   }

   @Test
   @DisplayName("Should return the current notification without saving when it is already active")
   void activateNotification_whenAlreadyActive_skipsSave() {
      final var entity = SubscriberNotificationEntity.builder()
         .subscription(mock(SubscriptionEntity.class))
         .rangeNotificationDays(3)
         .nextNotificationDate(LocalDateTime.of(2026, 8, 28, 8, 30))
         .notifiable(true)
         .build();
      final var expected = new SubscriberNotificationResponse(3, "2026-08-28T08:30", true);

      given(retrieverRepository.findByCurrentUsername(principal.username())).willReturn(Optional.of(entity));
      given(factory.createFromEntity(entity)).willReturn(expected);

      final var result = service.activateNotification(principal);

      assertAll(
         () -> assertSame(expected, result),
         () -> assertTrue(entity.isNotifiable())
      );

      verify(retrieverRepository).findByCurrentUsername("user@mail.com");
      verify(mutationRepository, never()).save(any());
      verify(factory).createFromEntity(entity);
      verifyNoMoreInteractions(retrieverRepository, mutationRepository, factory);
   }

   @Test
   @DisplayName("Should throw NotFoundException and skip persistence when no record exists for the user")
   void activateNotification_whenAbsent_throwsNotFoundException() {
      willThrow(new NotFoundException("Notification record not found for: user@mail.com"))
         .given(retrieverRepository).findByCurrentUsername(principal.username());

      assertThrows(NotFoundException.class, () -> service.activateNotification(principal));

      verify(retrieverRepository).findByCurrentUsername("user@mail.com");
      verifyNoInteractions(mutationRepository, factory);
   }

   @Test
   @DisplayName("Should not flag an already inactive entity as active when retrieval fails")
   void activateNotification_keepsEntityUntouchedOnFailure() {
      final var entity = SubscriberNotificationEntity.builder()
         .subscription(mock(SubscriptionEntity.class))
         .rangeNotificationDays(7)
         .notifiable(false)
         .build();

      given(retrieverRepository.findByCurrentUsername(principal.username())).willReturn(Optional.empty());

      assertThrows(NotFoundException.class, () -> service.activateNotification(principal));

      assertFalse(entity.isNotifiable(), "Entity must remain untouched when the record is absent.");
      verifyNoInteractions(mutationRepository, factory);
   }
}
