package com.jame.dev.gymApp.features.customer.infrastructure.adapter;

import com.jame.dev.gymApp.features.customer.domain.exception.CustomerNotFoundException;
import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerMutationRepository;
import com.jame.dev.gymApp.features.customer.infrastructure.persistence.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
@RequiredArgsConstructor
public class CustomerMutationRepositoryJpaAdapter implements CustomerMutationRepository {
   private final CustomerRepository customerRepository;

   @Override
   public CustomerEntity save(CustomerEntity customerEntity) {
      return customerRepository.saveAndFlush(customerEntity);
   }

   @Override
   public CustomerEntity recoverByUserEmail(String email) {
      final var customerEntity = customerRepository.findDeactivatedByUserEmail(email)
         .orElseThrow(() -> new CustomerNotFoundException("Customer Not found for email: " + email));
      customerEntity.setActive(true);
      customerEntity.setUpdatedAt(Instant.now());

      return customerEntity;
   }

   @Override
   public void deleteById(long id) {
      customerRepository.deleteById(id);
   }

   @Override
   public void deleteByUserId(long userId) {
      customerRepository.deleteByUser_Id(userId);
   }

   @Override
   public void hardDeleteById(long id) {
      customerRepository.hardDeleteById(id);
   }

   @Override
   public void activateCustomerByUserId(long userId) {
      customerRepository.activateByUserId(userId);
   }
}
