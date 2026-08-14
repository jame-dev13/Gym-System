package com.jame.dev.gymApp.features.subscription.domain.repository;

import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;

public interface SubscriptionMutationRepository {

   SubscriptionEntity save(final SubscriptionEntity subscriptionEntity);

   void deleteById(long id);

   void deleteByCustomerEmail(final String email);

}
