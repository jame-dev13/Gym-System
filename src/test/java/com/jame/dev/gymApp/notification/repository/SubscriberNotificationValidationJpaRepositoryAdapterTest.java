package com.jame.dev.gymApp.notification.repository;

import com.jame.dev.gymApp.features.notification.infrastructure.adapter.SubscriberNotificationValidationJpaRepositoryAdapter;
import com.jame.dev.gymApp.features.notification.infrastructure.persistence.SubscriberNotificationRepository;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriberNotificationValidationJpaRepositoryAdapterTest {

   @Mock
   private SubscriberNotificationRepository subscriberNotificationRepository;

   @InjectMocks
   private SubscriberNotificationValidationJpaRepositoryAdapter adapter;

   @Test
   @DisplayName("Should delegate existsById to JPA repository")
   void existsById_delegatesToJpaRepository() {
      var uuid = UUID.randomUUID();
      given(subscriberNotificationRepository.existsById(uuid)).willReturn(true);

      var result = adapter.existsById(uuid);

      assertTrue(result);
      verify(subscriberNotificationRepository).existsById(uuid);
      verifyNoMoreInteractions(subscriberNotificationRepository);
   }

   @Test
   @DisplayName("Should return false when entity not found by id")
   void existsById_whenNotFound_returnsFalse() {
      var uuid = UUID.randomUUID();
      given(subscriberNotificationRepository.existsById(uuid)).willReturn(false);

      var result = adapter.existsById(uuid);

      assertFalse(result);
      verify(subscriberNotificationRepository).existsById(uuid);
      verifyNoMoreInteractions(subscriberNotificationRepository);
   }

   @Test
   @DisplayName("Should delegate existsBySubscriber to JPA repository")
   void existsBySubscriber_delegatesToJpaRepository() {
      var subscription = new SubscriptionEntity();
      given(subscriberNotificationRepository.existsBySubscription(subscription)).willReturn(true);

      var result = adapter.existsBySubscriber(subscription);

      assertTrue(result);
      verify(subscriberNotificationRepository).existsBySubscription(subscription);
      verifyNoMoreInteractions(subscriberNotificationRepository);
   }

   @Test
   @DisplayName("Should return false when subscription has no notification")
   void existsBySubscriber_whenNotFound_returnsFalse() {
      var subscription = new SubscriptionEntity();
      given(subscriberNotificationRepository.existsBySubscription(subscription)).willReturn(false);

      var result = adapter.existsBySubscriber(subscription);

      assertFalse(result);
      verify(subscriberNotificationRepository).existsBySubscription(subscription);
      verifyNoMoreInteractions(subscriberNotificationRepository);
   }
}
