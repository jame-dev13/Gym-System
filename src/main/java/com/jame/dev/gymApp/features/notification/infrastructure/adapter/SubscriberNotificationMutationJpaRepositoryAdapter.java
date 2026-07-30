package com.jame.dev.gymApp.features.notification.infrastructure.adapter;

import com.jame.dev.gymApp.features.notification.domain.model.SubscriberNotificationEntity;
import com.jame.dev.gymApp.features.notification.domain.repository.SubscriberNotificationMutationRepository;
import com.jame.dev.gymApp.features.notification.infrastructure.persistence.SubscriberNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SubscriberNotificationMutationJpaRepositoryAdapter implements SubscriberNotificationMutationRepository {
   private final SubscriberNotificationRepository subscriberNotificationRepository;

   @Override
   public SubscriberNotificationEntity save(SubscriberNotificationEntity entity) {
      return subscriberNotificationRepository.saveAndFlush(entity);
   }

   @Override
   public void deleteById(UUID uuid) {
      subscriberNotificationRepository.deleteById(uuid);
   }
}
