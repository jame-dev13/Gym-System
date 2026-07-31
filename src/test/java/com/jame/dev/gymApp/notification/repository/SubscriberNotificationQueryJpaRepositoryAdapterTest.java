package com.jame.dev.gymApp.notification.repository;

import com.jame.dev.gymApp.features.notification.domain.model.SubscriberNotificationEntity;
import com.jame.dev.gymApp.features.notification.infrastructure.adapter.SubscriberNotificationQueryJpaRepositoryAdapter;
import com.jame.dev.gymApp.features.notification.infrastructure.persistence.SubscriberNotificationRepository;
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
   @DisplayName("Should delegate findBySubscriptionId to JPA repository")
   void findBySubscriptionId_delegatesToJpaRepository() {
      long subscriptionId = 1L;
      var entity = new SubscriberNotificationEntity();
      given(subscriberNotificationRepository.findBySubscriptionId(subscriptionId)).willReturn(Optional.of(entity));

      var result = adapter.findBySubscriptionId(subscriptionId);

      assertTrue(result.isPresent());
      assertSame(entity, result.get());
      verify(subscriberNotificationRepository).findBySubscriptionId(subscriptionId);
      verifyNoMoreInteractions(subscriberNotificationRepository);
   }

   @Test
   @DisplayName("Should return empty when subscription id has no notification")
   void findBySubscriptionId_whenNotFound_returnsEmpty() {
      long subscriptionId = 1L;
      given(subscriberNotificationRepository.findBySubscriptionId(subscriptionId)).willReturn(Optional.empty());

      var result = adapter.findBySubscriptionId(subscriptionId);

      assertTrue(result.isEmpty());
      verify(subscriberNotificationRepository).findBySubscriptionId(subscriptionId);
      verifyNoMoreInteractions(subscriberNotificationRepository);
   }

   @Test
   @DisplayName("Should delegate findBySubscriberId to JPA repository")
   void findBySubscriberId_delegatesToJpaRepository() {
      long subscriberId = 1L;
      var entity = new SubscriberNotificationEntity();
      given(subscriberNotificationRepository.findBySubscriptionId(subscriberId)).willReturn(Optional.of(entity));

      var result = adapter.findBySubscriberId(subscriberId);

      assertTrue(result.isPresent());
      assertSame(entity, result.get());
      verify(subscriberNotificationRepository).findBySubscriptionId(subscriberId);
      verifyNoMoreInteractions(subscriberNotificationRepository);
   }

   @Test
   @DisplayName("Should return empty when subscriber id has no notification")
   void findBySubscriberId_whenNotFound_returnsEmpty() {
      long subscriberId = 1L;
      given(subscriberNotificationRepository.findBySubscriptionId(subscriberId)).willReturn(Optional.empty());

      var result = adapter.findBySubscriberId(subscriberId);

      assertTrue(result.isEmpty());
      verify(subscriberNotificationRepository).findBySubscriptionId(subscriberId);
      verifyNoMoreInteractions(subscriberNotificationRepository);
   }
}
