package com.jame.dev.gymApp.repository;

import com.jame.dev.gymApp.entity.SubscriptionEntity;
import com.jame.dev.gymApp.repository.common.CustomJpaRepository;
import lombok.NonNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SubscriptionRepository extends CustomJpaRepository<SubscriptionEntity, Long> {
   boolean existsByCustomer_IdAndFinishedFalse(final long id);

   @Query("""
           SELECT s FROM SubscriptionEntity s
           JOIN FETCH s.customer c
           JOIN FETCH s.user u
           WHERE u.email = :email AND s.active = true
           """)
   Optional<SubscriptionEntity> findActiveSubscriptionByEmail(@Param("email") @NonNull final String email);
}
