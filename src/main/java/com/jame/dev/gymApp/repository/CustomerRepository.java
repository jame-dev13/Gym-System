package com.jame.dev.gymApp.repository;

import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.repository.common.CustomJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerRepository extends CustomJpaRepository<CustomerEntity, Long> {
   @Query("""
           SELECT c
           FROM CustomerEntity c
           WHERE c.user.email = :email
           """)
   Optional<CustomerEntity> findByUser_EmailAndActiveTrue(@Param("email") final String email);
   boolean existsByUser_EmailAndActiveTrue(final String email);
   boolean existsByIdAndUser_EmailAndActiveTrue(long id, String email);
}
