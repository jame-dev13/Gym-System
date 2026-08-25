package com.jame.dev.gymApp.notifications.usecases.query;

import com.jame.dev.gymApp.domain.exception.NotFoundException;
import com.jame.dev.gymApp.features.auth.domain.model.AuthPrincipal;
import com.jame.dev.gymApp.features.auth.domain.model.UserPrincipal;
import com.jame.dev.gymApp.features.notification.api.response.SubscriberNotificationResponse;
import com.jame.dev.gymApp.features.notification.application.contract.SubscriberNotificationFactory;
import com.jame.dev.gymApp.features.notification.application.service.query.GetCurrentSubscriberNotificationUseCaseService;
import com.jame.dev.gymApp.features.notification.domain.model.SubscriberNotificationEntity;
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
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class GetCurrentSubscriberNotificationUseCaseServiceTest {

   @Mock
   private SubscriberNotifiableRetrieverRepository subscriberNotifiableRetrieverRepository;

   @Mock
   private SubscriberNotificationFactory subscriberNotificationFactory;

   @InjectMocks
   private GetCurrentSubscriberNotificationUseCaseService service;

   private final AuthPrincipal principal = UserPrincipal.builder()
      .id(1L)
      .username("user@mail.com")
      .password("encoded")
      .authorities(Collections.emptyList())
      .build();

   @Test
   @DisplayName("Should return the current notification mapped by the factory when it exists")
   void getCurrent_whenExists_returnsMappedResponse() {
      final var entity = SubscriberNotificationEntity.builder()
         .subscription(mock(SubscriptionEntity.class))
         .rangeNotificationDays(7)
         .nextNotificationDate(LocalDateTime.of(2026, 9, 1, 12, 0))
         .notifiable(true)
         .build();
      final var expected = new SubscriberNotificationResponse(7, "2026-09-01T12:00", true);

      given(subscriberNotifiableRetrieverRepository.findByCurrentUsername(principal.username()))
         .willReturn(Optional.of(entity));
      given(subscriberNotificationFactory.createFromEntity(entity)).willReturn(expected);

      final var result = assertDoesNotThrow(() -> service.getCurrent(principal));

      assertAll(
         () -> assertNotNull(result, "Result should not be null."),
         () -> assertSame(expected, result, "Result should be the factory built response.")
      );

      verify(subscriberNotifiableRetrieverRepository).findByCurrentUsername("user@mail.com");
      verify(subscriberNotificationFactory).createFromEntity(entity);
      verifyNoMoreInteractions(subscriberNotifiableRetrieverRepository, subscriberNotificationFactory);
   }

   @Test
   @DisplayName("Should throw NotFoundException when no notification record exists for the authenticated user")
   void getCurrent_whenAbsent_throwsNotFoundException() {
      given(subscriberNotifiableRetrieverRepository.findByCurrentUsername(principal.username()))
         .willReturn(Optional.empty());

      final var exception = assertThrows(NotFoundException.class, () -> service.getCurrent(principal));

      assertTrue(exception.getMessage().contains("user@mail.com"),
         "Message should mention the missing username.");

      verify(subscriberNotifiableRetrieverRepository).findByCurrentUsername("user@mail.com");
      verifyNoInteractions(subscriberNotificationFactory);
   }

   @Test
   @DisplayName("Should forward the exact principal username to the retriever")
   void getCurrent_forwardsExactUsername() {
      final AuthPrincipal other = UserPrincipal.builder()
         .id(2L)
         .username("other@mail.com")
         .authorities(Collections.emptyList())
         .build();
      final var entity = new SubscriberNotificationEntity();

      given(subscriberNotifiableRetrieverRepository.findByCurrentUsername("other@mail.com"))
         .willReturn(Optional.of(entity));
      given(subscriberNotificationFactory.createFromEntity(entity))
         .willReturn(new SubscriberNotificationResponse(3, "2026-08-28T00:00", false));

      final var result = service.getCurrent(other);

      assertEquals(false, result.notifiable());
      verify(subscriberNotifiableRetrieverRepository).findByCurrentUsername("other@mail.com");
      verifyNoMoreInteractions(subscriberNotifiableRetrieverRepository);
   }
}
