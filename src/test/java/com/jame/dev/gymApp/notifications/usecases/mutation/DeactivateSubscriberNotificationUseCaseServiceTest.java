package com.jame.dev.gymApp.notifications.usecases.mutation;

import com.jame.dev.gymApp.domain.exception.NotFoundException;
import com.jame.dev.gymApp.features.auth.domain.model.AuthPrincipal;
import com.jame.dev.gymApp.features.auth.domain.model.UserPrincipal;
import com.jame.dev.gymApp.features.notification.api.response.SubscriberNotificationResponse;
import com.jame.dev.gymApp.features.notification.application.contract.SubscriberNotificationFactory;
import com.jame.dev.gymApp.features.notification.application.service.mutation.DeactivateSubscriberNotificationUseCaseService;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class DeactivateSubscriberNotificationUseCaseServiceTest {

   @Mock
   private SubscriberNotifiableRetrieverRepository retrieverRepository;

   @Mock
   private SubscriberNotificationMutationRepository mutationRepository;

   @Mock
   private SubscriberNotificationFactory factory;

   @InjectMocks
   private DeactivateSubscriberNotificationUseCaseService service;

   private final AuthPrincipal principal = UserPrincipal.builder()
      .id(1L)
      .username("user@mail.com")
      .password("encoded")
      .authorities(Collections.emptyList())
      .build();

   @Test
   @DisplayName("Should deactivate the notification, persist it and return the mapped response when it was active")
   void deactivateNotification_whenActive_deactivatesAndSaves() {
      final var entity = SubscriberNotificationEntity.builder()
         .subscription(mock(SubscriptionEntity.class))
         .rangeNotificationDays(7)
         .nextNotificationDate(LocalDateTime.of(2026, 9, 1, 12, 0))
         .notifiable(true)
         .build();
      final var expected = new SubscriberNotificationResponse(7, "2026-09-01T12:00", false);

      given(retrieverRepository.findByCurrentUsername(principal.username())).willReturn(Optional.of(entity));
      given(mutationRepository.save(entity)).willReturn(entity);
      given(factory.createFromEntity(entity)).willReturn(expected);

      final var result = service.deactivateNotification(principal);

      assertAll(
         () -> assertNotNull(result, "Result should not be null."),
         () -> assertSame(expected, result, "Result should be the factory built response."),
         () -> assertTrue(!entity.isNotifiable(), "Entity should be flagged as not notifiable before saving.")
      );

      verify(retrieverRepository).findByCurrentUsername("user@mail.com");
      verify(mutationRepository).save(entity);
      verify(factory).createFromEntity(entity);
      verifyNoMoreInteractions(retrieverRepository, mutationRepository, factory);
   }

   @Test
   @DisplayName("Should return the current notification without saving when it is already inactive")
   void deactivateNotification_whenAlreadyInactive_skipsSave() {
      final var entity = SubscriberNotificationEntity.builder()
         .subscription(mock(SubscriptionEntity.class))
         .rangeNotificationDays(4)
         .nextNotificationDate(LocalDateTime.of(2026, 8, 29, 9, 0))
         .notifiable(false)
         .build();
      final var expected = new SubscriberNotificationResponse(4, "2026-08-29T09:00", false);

      given(retrieverRepository.findByCurrentUsername(principal.username())).willReturn(Optional.of(entity));
      given(factory.createFromEntity(entity)).willReturn(expected);

      final var result = service.deactivateNotification(principal);

      assertSame(expected, result);

      verify(retrieverRepository).findByCurrentUsername("user@mail.com");
      verify(mutationRepository, never()).save(any());
      verify(factory).createFromEntity(entity);
      verifyNoMoreInteractions(retrieverRepository, mutationRepository, factory);
   }

   @Test
   @DisplayName("Should throw NotFoundException and skip persistence when no record exists for the user")
   void deactivateNotification_whenAbsent_throwsNotFoundException() {
      given(retrieverRepository.findByCurrentUsername(principal.username())).willReturn(Optional.empty());

      assertThrows(NotFoundException.class, () -> service.deactivateNotification(principal));

      verify(retrieverRepository).findByCurrentUsername("user@mail.com");
      verifyNoInteractions(mutationRepository, factory);
   }
}
