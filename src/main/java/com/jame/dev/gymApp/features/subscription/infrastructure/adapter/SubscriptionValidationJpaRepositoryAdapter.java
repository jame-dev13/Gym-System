package com.jame.dev.gymApp.features.subscription.infrastructure.adapter;

import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionStatus;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionValidationRepository;
import com.jame.dev.gymApp.features.subscription.infrastructure.persistence.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SubscriptionValidationJpaRepositoryAdapter implements SubscriptionValidationRepository {
   private final SubscriptionRepository subscriptionRepository;

   @Override
   public boolean existsById(long id) {
      return subscriptionRepository.existsById(id);
   }

   @Override
   public boolean existsByIdAndCustomerEmail(long subscriptionId, String email) {
      return subscriptionRepository.existsByIdAndCustomer_User_Email(subscriptionId, email);
   }

   @Override
   public boolean existsByCustomer(CustomerEntity customerEntity) {
      return subscriptionRepository.existsByCustomer(customerEntity);
   }

   @Override
   public boolean existsByCustomerEmail(String username) {
      return subscriptionRepository.existsByCustomer_User_Email(username);
   }

   @Override
   public boolean existsByCustomerEmailAndStatus(String username, SubscriptionStatus status) {
      return subscriptionRepository.existsByCustomer_User_EmailAndStatus(username, status);
   }
}
