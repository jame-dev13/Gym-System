package com.jame.dev.gymApp.features.customer.infrastructure.adapter;

import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerQueryRepository;
import com.jame.dev.gymApp.features.customer.infrastructure.persistence.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CustomerQueryRepositoryJpaAdapter implements CustomerQueryRepository {
   private final CustomerRepository customerRepository;

   @Override
   public Page<CustomerEntity> findAll(Specification<CustomerEntity> specification, Pageable pageable) {
      return customerRepository.findAll(specification, pageable);
   }

   @Override
   public Optional<CustomerEntity> findById(long id) {
      return customerRepository.findById(id);
   }

   @Override
   public Optional<CustomerEntity> findByUserEmail(String email) {
      return customerRepository.findByUser_Email(email);
   }

   @Override
   public Optional<CustomerEntity> findDeactivatedById(long id) {
      return customerRepository.findDeactivatedById(id);
   }
}
