package com.jame.dev.gymApp.notification.repository;

import com.jame.dev.gymApp.features.notification.domain.model.SubscriberNotificationEntity;
import com.jame.dev.gymApp.features.notification.infrastructure.adapter.SubscriberNotificationQueryJpaRepositoryAdapter;
import com.jame.dev.gymApp.features.notification.infrastructure.persistence.SubscriberNotificationRepository;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
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
class SubscriberNotificationQueryJpaRepositoryAdapterTest {

   @Mock
   private SubscriberNotificationRepository subscriberNotificationRepository;

   @InjectMocks
   private SubscriberNotificationQueryJpaRepositoryAdapter adapter;

   @Test
   @DisplayName("Should delegate findById to JPA repository")
   void findById_delegatesToJpaRepository() {
      var uuid = UUID.randomUUID();
      var entity = new SubscriberNotificationEntity();
      given(subscriberNotificationRepository.findById(uuid)).willReturn(Optional.of(entity));

      var result = adapter.findById(uuid);

      assertTrue(result.isPresent());
      assertSame(entity, result.get());
      verify(subscriberNotificationRepository).findById(uuid);
      verifyNoMoreInteractions(subscriberNotificationRepository);
   }

   @Test
   @DisplayName("Should return empty when JPA repository returns empty")
   void findById_whenNotFound_returnsEmpty() {
      var uuid = UUID.randomUUID();
      given(subscriberNotificationRepository.findById(uuid)).willReturn(Optional.empty());

      var result = adapter.findById(uuid);

      assertTrue(result.isEmpty());
      verify(subscriberNotificationRepository).findById(uuid);
      verifyNoMoreInteractions(subscriberNotificationRepository);
   }

   @Test
   @DisplayName("Should delegate findBySubscriber to JPA repository")
   void findBySubscriber_delegatesToJpaRepository() {
      var subscription = new SubscriptionEntity();
      var entity = new SubscriberNotificationEntity();
      given(subscriberNotificationRepository.findBySubscription(subscription)).willReturn(Optional.of(entity));

      var result = adapter.findBySubscriber(subscription);

      assertTrue(result.isPresent());
      assertSame(entity, result.get());
      verify(subscriberNotificationRepository).findBySubscription(subscription);
      verifyNoMoreInteractions(subscriberNotificationRepository);
   }

   @Test
   @DisplayName("Should return empty when subscription has no notification")
   void findBySubscriber_whenNotFound_returnsEmpty() {
      var subscription = new SubscriptionEntity();
      given(subscriberNotificationRepository.findBySubscription(subscription)).willReturn(Optional.empty());

      var result = adapter.findBySubscriber(subscription);

      assertTrue(result.isEmpty());
      verify(subscriberNotificationRepository).findBySubscription(subscription);
      verifyNoMoreInteractions(subscriberNotificationRepository);
   }
}
