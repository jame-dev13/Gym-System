package com.jame.dev.gymApp.repository;

import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.repository.common.CustomJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerRepository extends CustomJpaRepository<CustomerEntity, Long> {
   @Query("""
           SELECT c.user FROM CustomerEntity c WHERE c.user.id = :id
           """)
   Optional<UserEntity> findUserAssociatedByIdUser(@Param("id") final long id);
}
