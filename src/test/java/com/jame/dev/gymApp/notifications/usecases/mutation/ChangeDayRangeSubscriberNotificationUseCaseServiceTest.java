package com.jame.dev.gymApp.notifications.usecases.mutation;

import com.jame.dev.gymApp.domain.exception.NotFoundException;
import com.jame.dev.gymApp.features.auth.domain.model.AuthPrincipal;
import com.jame.dev.gymApp.features.auth.domain.model.UserPrincipal;
import com.jame.dev.gymApp.features.notification.api.request.DayRangeRequest;
import com.jame.dev.gymApp.features.notification.api.response.SubscriberNotificationResponse;
import com.jame.dev.gymApp.features.notification.application.contract.SubscriberNotificationFactory;
import com.jame.dev.gymApp.features.notification.application.service.mutation.ChangeDayRangeSubscriberNotificationUseCaseService;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class ChangeDayRangeSubscriberNotificationUseCaseServiceTest {

   private static final LocalDateTime NEXT_DATE = LocalDateTime.of(2026, 9, 1, 12, 0);

   @Mock
   private SubscriberNotifiableRetrieverRepository retrieverRepository;

   @Mock
   private SubscriberNotificationMutationRepository mutationRepository;

   @Mock
   private SubscriberNotificationFactory factory;

   @InjectMocks
   private ChangeDayRangeSubscriberNotificationUseCaseService service;

   private final AuthPrincipal principal = UserPrincipal.builder()
      .id(1L)
      .username("user@mail.com")
      .password("encoded")
      .authorities(Collections.emptyList())
      .build();

   @Test
   @DisplayName("Should return the current notification without saving when requested range equals the current one")
   void changeDayRange_whenSameRange_skipsSave() {
      final var entity = SubscriberNotificationEntity.builder()
         .subscription(mock(SubscriptionEntity.class))
         .rangeNotificationDays(7)
         .nextNotificationDate(NEXT_DATE)
         .notifiable(true)
         .build();
      final var expected = new SubscriberNotificationResponse(7, "2026-09-01T12:00", true);

      given(retrieverRepository.findByCurrentUsername(principal.username())).willReturn(Optional.of(entity));
      given(factory.createFromEntity(entity)).willReturn(expected);

      final var result = service.changeDayRange(principal, new DayRangeRequest(7));

      assertAll(
         () -> assertSame(expected, result),
         () -> assertEquals(7, entity.getRangeNotificationDays(), "Range should remain unchanged."),
         () -> assertEquals(NEXT_DATE, entity.getNextNotificationDate(), "Next date should remain unchanged.")
      );

      verify(retrieverRepository).findByCurrentUsername("user@mail.com");
      verify(mutationRepository, never()).save(any());
      verify(factory).createFromEntity(entity);
      verifyNoMoreInteractions(retrieverRepository, mutationRepository, factory);
   }

   @Test
   @DisplayName("Should shrink range and postpone the next notification date when decreasing the day range")
   void changeDayRange_whenShrinking_postponesNextDate() {
      final var entity = SubscriberNotificationEntity.builder()
         .subscription(mock(SubscriptionEntity.class))
         .rangeNotificationDays(7)
         .nextNotificationDate(NEXT_DATE)
         .notifiable(true)
         .build();
      final var expected = new SubscriberNotificationResponse(3, "2026-09-05T12:00", true);

      given(retrieverRepository.findByCurrentUsername(principal.username())).willReturn(Optional.of(entity));
      given(mutationRepository.save(entity)).willReturn(entity);
      given(factory.createFromEntity(entity)).willReturn(expected);

      final var result = service.changeDayRange(principal, new DayRangeRequest(3));

      assertAll(
         () -> assertNotNull(result),
         () -> assertSame(expected, result),
         () -> assertEquals(3, entity.getRangeNotificationDays()),
         () -> assertEquals(LocalDateTime.of(2026, 9, 5, 12, 0), entity.getNextNotificationDate(),
            "Difference of +4 days should be added to the next notification date.")
      );

      verify(retrieverRepository).findByCurrentUsername("user@mail.com");
      verify(mutationRepository).save(entity);
      verify(factory).createFromEntity(entity);
      verifyNoMoreInteractions(retrieverRepository, mutationRepository, factory);
   }

   @Test
   @DisplayName("Should grow range and anticipate the next notification date when increasing the day range")
   void changeDayRange_whenGrowing_anticipatesNextDate() {
      final var entity = SubscriberNotificationEntity.builder()
         .subscription(mock(SubscriptionEntity.class))
         .rangeNotificationDays(3)
         .nextNotificationDate(NEXT_DATE)
         .notifiable(true)
         .build();
      final var expected = new SubscriberNotificationResponse(7, "2026-08-28T12:00", true);

      given(retrieverRepository.findByCurrentUsername(principal.username())).willReturn(Optional.of(entity));
      given(mutationRepository.save(entity)).willReturn(entity);
      given(factory.createFromEntity(entity)).willReturn(expected);

      final var result = service.changeDayRange(principal, new DayRangeRequest(7));

      assertAll(
         () -> assertSame(expected, result),
         () -> assertEquals(7, entity.getRangeNotificationDays()),
         () -> assertEquals(LocalDateTime.of(2026, 8, 28, 12, 0), entity.getNextNotificationDate(),
            "Difference of -4 days should be subtracted from the next notification date.")
      );

      verify(mutationRepository).save(entity);
      verify(factory).createFromEntity(entity);
      verifyNoMoreInteractions(retrieverRepository, mutationRepository, factory);
   }

   @Test
   @DisplayName("Should throw NullPointerException mentioning the undefined next date when it is null and range differs")
   void changeDayRange_whenNextDateNull_throwsNullPointerException() {
      final var entity = SubscriberNotificationEntity.builder()
         .subscription(mock(SubscriptionEntity.class))
         .rangeNotificationDays(7)
         .nextNotificationDate(null)
         .notifiable(true)
         .build();

      given(retrieverRepository.findByCurrentUsername(principal.username())).willReturn(Optional.of(entity));

      final var exception = assertThrows(NullPointerException.class,
         () -> service.changeDayRange(principal, new DayRangeRequest(5)));

      assertEquals("Next notification date found as undefined.", exception.getMessage());
      verify(retrieverRepository).findByCurrentUsername("user@mail.com");
      verifyNoInteractions(mutationRepository, factory);
   }

   @Test
   @DisplayName("Should throw NotFoundException and skip persistence when no record exists for the user")
   void changeDayRange_whenAbsent_throwsNotFoundException() {
      given(retrieverRepository.findByCurrentUsername(principal.username())).willReturn(Optional.empty());

      assertThrows(NotFoundException.class,
         () -> service.changeDayRange(principal, new DayRangeRequest(5)));

      verify(retrieverRepository).findByCurrentUsername("user@mail.com");
      verifyNoInteractions(mutationRepository, factory);
   }
}
