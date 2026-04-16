package com.jame.dev.gymApp.repository;

import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.entity.SubscriptionEntity;
import com.jame.dev.gymApp.repository.common.CustomJpaRepository;
import lombok.NonNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SubscriptionRepository extends CustomJpaRepository<SubscriptionEntity, Long> {
   @Query("""
           SELECT s FROM SubscriptionEntity s
           JOIN FETCH s.customer c
           JOIN FETCH c.user u
           WHERE u.email = :email AND s.active = true
           """)
   Optional<SubscriptionEntity> findActiveSubscriptionByEmail(@Param("email") @NonNull final String email);

   boolean existsByIdAndCustomer_User_EmailAndActiveTrue(long id, String email);

   boolean existsByCustomer(final CustomerEntity customer);

   void deleteByCustomerId(long id);
}
