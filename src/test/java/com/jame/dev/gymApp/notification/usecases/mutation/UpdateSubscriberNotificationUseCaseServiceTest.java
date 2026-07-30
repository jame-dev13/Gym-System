package com.jame.dev.gymApp.notification.usecases.mutation;

import com.jame.dev.gymApp.features.notification.api.request.SubscriberNotificationUpdateRequest;
import com.jame.dev.gymApp.features.notification.api.response.SubscriberNotificationResponse;
import com.jame.dev.gymApp.features.notification.application.contract.SubscriberNotificationFactory;
import com.jame.dev.gymApp.features.notification.application.service.mutation.UpdateSubscriberNotificationUseCaseService;
import com.jame.dev.gymApp.features.notification.domain.exception.NotificationException;
import com.jame.dev.gymApp.features.notification.domain.model.SubscriberNotificationEntity;
import com.jame.dev.gymApp.features.notification.domain.repository.SubscriberNotificationMutationRepository;
import com.jame.dev.gymApp.features.notification.domain.repository.SubscriberNotificationQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateSubscriberNotificationUseCaseServiceTest {

   @Mock
   private SubscriberNotificationQueryRepository subscriberNotificationQueryRepository;

   @Mock
   private SubscriberNotificationMutationRepository subscriberNotificationMutationRepository;

   @Mock
   private SubscriberNotificationFactory subscriberNotificationFactory;

   @InjectMocks
   private UpdateSubscriberNotificationUseCaseService service;

   @Captor
   private ArgumentCaptor<SubscriberNotificationEntity> entityCaptor;

   private final SubscriberNotificationUpdateRequest request = new SubscriberNotificationUpdateRequest(5);

   @Test
   @DisplayName("Should update notification when found")
   void update_whenFound_updatesAndReturnsResponse() {
      var uuid = UUID.randomUUID();
      var entity = new SubscriberNotificationEntity();
      entity.setRangeNotificationDays(3);
      var savedEntity = new SubscriberNotificationEntity();
      var response = mock(SubscriberNotificationResponse.class);

      given(subscriberNotificationQueryRepository.findById(uuid)).willReturn(Optional.of(entity));
      given(subscriberNotificationMutationRepository.save(entity)).willReturn(savedEntity);
      given(subscriberNotificationFactory.createFromEntity(savedEntity)).willReturn(response);

      var result = service.updateSubscriberNotification(uuid, request);

      assertSame(response, result);
      assertEquals(5, entity.getRangeNotificationDays());
      verify(subscriberNotificationQueryRepository).findById(uuid);
      verify(subscriberNotificationMutationRepository).save(entity);
      verify(subscriberNotificationFactory).createFromEntity(savedEntity);
      verifyNoMoreInteractions(subscriberNotificationQueryRepository,
         subscriberNotificationMutationRepository, subscriberNotificationFactory);
   }

   @Test
   @DisplayName("Should throw NotificationException when notification not found")
   void update_whenNotFound_throwsException() {
      var uuid = UUID.randomUUID();
      given(subscriberNotificationQueryRepository.findById(uuid)).willReturn(Optional.empty());

      assertThrows(NotificationException.class,
         () -> service.updateSubscriberNotification(uuid, request));

      verify(subscriberNotificationQueryRepository).findById(uuid);
      verifyNoInteractions(subscriberNotificationMutationRepository, subscriberNotificationFactory);
   }
}
