package com.jame.dev.gymApp.features.subscription.domain.repository;

import com.jame.dev.gymApp.domain.repository.CustomJpaRepository;
import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
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

   @Query(
      """
         SELECT s
         FROM SubscriptionEntity s
         JOIN FETCH s.subscriptionPeriods
         """)
   List<SubscriptionEntity> findAllNotifiable();

   @NativeQuery("""
      SELECT * FROM subscriptions s
      WHERE s.id = :id AND
      s.active = false
      """)
   Optional<SubscriptionEntity> findDeactivatedById(@Param("id") long id);
}
