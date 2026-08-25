package com.jame.dev.gymApp.features.subscription.domain.repository;

import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionStatus;

public interface SubscriptionValidationRepository {

   boolean existsById(final long id);

   boolean existsByIdAndCustomerEmail(final long subscriptionId, final String email);

   boolean existsByCustomer(final CustomerEntity customerEntity);

   boolean existsByCustomerEmail(final String username);

   boolean existsByCustomerEmailAndStatus(final String username, SubscriptionStatus status);
}
