package com.jame.dev.gymApp.notification.usecases.mutation;

import com.jame.dev.gymApp.features.notification.api.request.SubscriberNotificationRequest;
import com.jame.dev.gymApp.features.notification.api.response.SubscriberNotificationResponse;
import com.jame.dev.gymApp.features.notification.application.contract.SubscriberNotificationFactory;
import com.jame.dev.gymApp.features.notification.application.dto.SubscriberNotificationFactoryDtoInput;
import com.jame.dev.gymApp.features.notification.application.service.mutation.CreateSubscriberNotificationUseCaseService;
import com.jame.dev.gymApp.features.notification.domain.exception.NotificationException;
import com.jame.dev.gymApp.features.notification.domain.model.SubscriberNotificationEntity;
import com.jame.dev.gymApp.features.notification.domain.repository.SubscriberNotificationMutationRepository;
import com.jame.dev.gymApp.features.notification.domain.repository.SubscriberNotificationValidationRepository;
import com.jame.dev.gymApp.features.subscription.domain.exception.SubscriptionNotFoundException;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateSubscriberNotificationUseCaseServiceTest {

   @Mock
   private SubscriberNotificationMutationRepository subscriberNotificationMutationRepository;

   @Mock
   private SubscriberNotificationValidationRepository subscriberNotificationValidationRepository;

   @Mock
   private SubscriptionQueryRepository subscriptionQueryRepository;

   @Mock
   private SubscriberNotificationFactory subscriberNotificationFactory;

   @InjectMocks
   private CreateSubscriberNotificationUseCaseService service;

   @Captor
   private ArgumentCaptor<SubscriberNotificationEntity> entityCaptor;

   private final SubscriberNotificationRequest request = new SubscriberNotificationRequest(1L, 3);

   @Test
   @DisplayName("Should create notification when subscription exists and no duplicate")
   void create_whenValid_createsAndReturnsResponse() {
      var subscription = new SubscriptionEntity();
      var entity = new SubscriberNotificationEntity();
      var savedEntity = new SubscriberNotificationEntity();
      var response = mock(SubscriberNotificationResponse.class);

      given(subscriptionQueryRepository.findById(1L)).willReturn(Optional.of(subscription));
      given(subscriberNotificationValidationRepository.existsBySubscriber(subscription)).willReturn(false);
      given(subscriberNotificationFactory.createFromInput(any(SubscriberNotificationFactoryDtoInput.class)))
         .willReturn(entity);
      given(subscriberNotificationMutationRepository.save(entity)).willReturn(savedEntity);
      given(subscriberNotificationFactory.createFromEntity(savedEntity)).willReturn(response);

      var result = service.createSubscriberNotification(request);

      assertSame(response, result);
      verify(subscriptionQueryRepository).findById(1L);
      verify(subscriberNotificationValidationRepository).existsBySubscriber(subscription);
      verify(subscriberNotificationFactory).createFromInput(any(SubscriberNotificationFactoryDtoInput.class));
      verify(subscriberNotificationMutationRepository).save(entity);
      verify(subscriberNotificationFactory).createFromEntity(savedEntity);
      verifyNoMoreInteractions(subscriberNotificationMutationRepository,
         subscriberNotificationValidationRepository, subscriptionQueryRepository,
         subscriberNotificationFactory);
   }

   @Test
   @DisplayName("Should throw SubscriptionNotFoundException when subscription not found")
   void create_whenSubscriptionNotFound_throwsException() {
      given(subscriptionQueryRepository.findById(1L)).willReturn(Optional.empty());

      assertThrows(SubscriptionNotFoundException.class,
         () -> service.createSubscriberNotification(request));

      verify(subscriptionQueryRepository).findById(1L);
      verifyNoInteractions(subscriberNotificationValidationRepository,
         subscriberNotificationMutationRepository, subscriberNotificationFactory);
   }

   @Test
   @DisplayName("Should throw NotificationException when notification already exists")
   void create_whenAlreadyExists_throwsException() {
      var subscription = new SubscriptionEntity();
      given(subscriptionQueryRepository.findById(1L)).willReturn(Optional.of(subscription));
      given(subscriberNotificationValidationRepository.existsBySubscriber(subscription)).willReturn(true);

      assertThrows(NotificationException.class,
         () -> service.createSubscriberNotification(request));

      verify(subscriptionQueryRepository).findById(1L);
      verify(subscriberNotificationValidationRepository).existsBySubscriber(subscription);
      verifyNoInteractions(subscriberNotificationMutationRepository, subscriberNotificationFactory);
   }
}
