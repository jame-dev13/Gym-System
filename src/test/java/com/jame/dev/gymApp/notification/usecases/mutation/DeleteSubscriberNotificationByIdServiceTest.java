package com.jame.dev.gymApp.notification.usecases.mutation;

import com.jame.dev.gymApp.features.notification.application.service.mutation.DeleteSubscriberNotificationByIdService;
import com.jame.dev.gymApp.features.notification.domain.exception.NotificationException;
import com.jame.dev.gymApp.features.notification.domain.repository.SubscriberNotificationMutationRepository;
import com.jame.dev.gymApp.features.notification.domain.repository.SubscriberNotificationValidationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteSubscriberNotificationByIdServiceTest {

   @Mock
   private SubscriberNotificationValidationRepository subscriberNotificationValidationRepository;

   @Mock
   private SubscriberNotificationMutationRepository subscriberNotificationMutationRepository;

   @InjectMocks
   private DeleteSubscriberNotificationByIdService service;

   @Test
   @DisplayName("Should delete when notification exists")
   void deleteById_whenExists_deletes() {
      var uuid = UUID.randomUUID();
      given(subscriberNotificationValidationRepository.existsById(uuid)).willReturn(true);

      service.deleteSubscriberNotificationById(uuid);

      verify(subscriberNotificationValidationRepository).existsById(uuid);
      verify(subscriberNotificationMutationRepository).deleteById(uuid);
      verifyNoMoreInteractions(subscriberNotificationValidationRepository,
         subscriberNotificationMutationRepository);
   }

   @Test
   @DisplayName("Should throw NotificationException when notification not found")
   void deleteById_whenNotFound_throwsException() {
      var uuid = UUID.randomUUID();
      given(subscriberNotificationValidationRepository.existsById(uuid)).willReturn(false);

      assertThrows(NotificationException.class,
         () -> service.deleteSubscriberNotificationById(uuid));

      verify(subscriberNotificationValidationRepository).existsById(uuid);
      verifyNoInteractions(subscriberNotificationMutationRepository);
   }
}
