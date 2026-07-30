package com.jame.dev.gymApp.notification.repository;

import com.jame.dev.gymApp.features.notification.domain.model.SubscriberNotificationEntity;
import com.jame.dev.gymApp.features.notification.infrastructure.adapter.SubscriberNotificationMutationJpaRepositoryAdapter;
import com.jame.dev.gymApp.features.notification.infrastructure.persistence.SubscriberNotificationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriberNotificationMutationJpaRepositoryAdapterTest {

   @Mock
   private SubscriberNotificationRepository subscriberNotificationRepository;

   @InjectMocks
   private SubscriberNotificationMutationJpaRepositoryAdapter adapter;

   @Test
   @DisplayName("Should delegate save to JPA repository with saveAndFlush")
   void save_delegatesToJpaRepository() {
      var entity = new SubscriberNotificationEntity();
      given(subscriberNotificationRepository.saveAndFlush(entity)).willReturn(entity);

      adapter.save(entity);

      verify(subscriberNotificationRepository).saveAndFlush(entity);
      verifyNoMoreInteractions(subscriberNotificationRepository);
   }

   @Test
   @DisplayName("Should delegate deleteById to JPA repository")
   void deleteById_delegatesToJpaRepository() {
      var uuid = UUID.randomUUID();

      adapter.deleteById(uuid);

      verify(subscriberNotificationRepository).deleteById(uuid);
      verifyNoMoreInteractions(subscriberNotificationRepository);
   }
}
