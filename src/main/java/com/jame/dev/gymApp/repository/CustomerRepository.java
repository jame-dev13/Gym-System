package com.jame.dev.gymApp.repository;

import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.repository.common.CustomJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerRepository extends CustomJpaRepository<CustomerEntity, Long> {
   @Query(nativeQuery = true, value = """
           SELECT c.* FROM customers c JOIN users u ON u.id = c.user_id WHERE u.email = :userEmail
           """)
   Optional<CustomerEntity> findDeactivatedByUser_email(@Param("userEmail") final String userEmail);

   Optional<CustomerEntity> findByUser_Email(final String email);

   @Query(nativeQuery = true, value = """
           SELECT c.* FROM customers c WHERE c.user_id = :userId
           """)
   Optional<CustomerEntity> findDeactivatedByUserId(@Param("userId") final long userId);

   boolean existsByIdAndUser_EmailAndActiveTrue(long id, String email);

   @Query(nativeQuery = true, value = """
           SELECT EXISTS(
                    SELECT 1 FROM customers c
                    WHERE c.user_id = :userId)
           """)
   boolean existsDeactivatedByUserId(@Param("userId") final long userId);

   void deleteByUserId(long userId);
}
