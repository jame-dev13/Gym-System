package com.jame.dev.gymApp.features.customer.domain.repository;

import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;

public interface CustomerMutationRepository {

   CustomerEntity save(final CustomerEntity customerEntity);

   CustomerEntity recoverByUserEmail(final String email);

   void deleteById(final long id);

   void deleteByUserId(final long userId);

   void hardDeleteById(final long id);

   void activateCustomerByUserId(long userId);
}
