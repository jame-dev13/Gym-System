package com.jame.dev.gymApp.features.subscription.infrastructure.adapter;

import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionMutationRepository;
import com.jame.dev.gymApp.features.subscription.infrastructure.persistence.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SubscriptionMutationJpaRepositoryAdapter implements SubscriptionMutationRepository {
   private final SubscriptionRepository subscriptionRepository;

   @Override
   public SubscriptionEntity save(SubscriptionEntity subscriptionEntity) {
      return subscriptionRepository.saveAndFlush(subscriptionEntity);
   }

   @Override
   public void deleteById(long id) {
      subscriptionRepository.deleteById(id);
   }
}
