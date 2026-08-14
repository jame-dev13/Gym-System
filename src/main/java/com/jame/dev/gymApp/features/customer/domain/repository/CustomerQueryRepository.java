package com.jame.dev.gymApp.features.customer.domain.repository;

import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

public interface CustomerQueryRepository {
   Page<CustomerEntity> findAll(final Specification<CustomerEntity> specification, final Pageable pageable);

   Optional<CustomerEntity> findById(final long id);

   Optional<CustomerEntity> findByUserEmail(final String email);

   Optional<CustomerEntity> findDeactivatedById(final long id);

   Optional<Long> findIdByUserEmail(final String email);
}
