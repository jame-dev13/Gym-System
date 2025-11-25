package com.jame.dev.gymApp.repository;

import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.repository.common.CustomJpaRepository;

public interface CustomerRepository extends CustomJpaRepository<CustomerEntity, Long> {
}
