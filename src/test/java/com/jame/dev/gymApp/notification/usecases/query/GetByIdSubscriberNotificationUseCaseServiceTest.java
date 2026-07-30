package com.jame.dev.gymApp.notification.usecases.query;

import com.jame.dev.gymApp.features.notification.api.response.SubscriberNotificationResponse;
import com.jame.dev.gymApp.features.notification.application.contract.SubscriberNotificationFactory;
import com.jame.dev.gymApp.features.notification.application.service.query.GetByIdSubscriberNotificationUseCaseService;
import com.jame.dev.gymApp.features.notification.domain.exception.NotificationException;
import com.jame.dev.gymApp.features.notification.domain.model.SubscriberNotificationEntity;
import com.jame.dev.gymApp.features.notification.domain.repository.SubscriberNotificationQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class GetByIdSubscriberNotificationUseCaseServiceTest {

   @Mock
   private SubscriberNotificationQueryRepository subscriberNotificationQueryRepository;

   @Mock
   private SubscriberNotificationFactory subscriberNotificationFactory;

   @InjectMocks
   private GetByIdSubscriberNotificationUseCaseService service;

   @Test
   @DisplayName("Should return response when notification exists")
   void getById_whenExists_returnsResponse() {
      var uuid = UUID.randomUUID();
      var entity = new SubscriberNotificationEntity();
      var response = mock(SubscriberNotificationResponse.class);

      given(subscriberNotificationQueryRepository.findById(uuid)).willReturn(Optional.of(entity));
      given(subscriberNotificationFactory.createFromEntity(entity)).willReturn(response);

      var result = service.getById(uuid);

      assertSame(response, result);
      verify(subscriberNotificationQueryRepository).findById(uuid);
      verify(subscriberNotificationFactory).createFromEntity(entity);
      verifyNoMoreInteractions(subscriberNotificationQueryRepository, subscriberNotificationFactory);
   }

   @Test
   @DisplayName("Should throw NotificationException when notification not found")
   void getById_whenNotFound_throwsException() {
      var uuid = UUID.randomUUID();
      given(subscriberNotificationQueryRepository.findById(uuid)).willReturn(Optional.empty());

      assertThrows(NotificationException.class, () -> service.getById(uuid));

      verify(subscriberNotificationQueryRepository).findById(uuid);
      verifyNoInteractions(subscriberNotificationFactory);
   }
}
