package com.jame.dev.gymApp.features.customer.domain.repository;

import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;

public interface CustomerMutationRepository {

   CustomerEntity save(final CustomerEntity customerEntity);

   CustomerEntity recoverByUserEmail(final String email);

   void deleteById(final long id);

   void hardDeleteById(final long id);

   void activateCustomerById(long userId);
}
