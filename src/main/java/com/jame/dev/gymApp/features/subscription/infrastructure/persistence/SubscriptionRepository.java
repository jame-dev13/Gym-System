package com.jame.dev.gymApp.features.subscription.infrastructure.persistence;

import com.jame.dev.gymApp.domain.repository.CustomJpaRepository;
import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.NativeQuery;
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
   Optional<SubscriptionEntity> findByCustomerEmail(@Param("email") @NonNull final String email);

   Page<SubscriptionEntity> findAllByCustomer_User_Email(String email, Pageable pageable);

   boolean existsByIdAndCustomer_User_Email(long id, String email);

   boolean existsByCustomer(final CustomerEntity customer);

   @NativeQuery("""
      SELECT * FROM subscriptions s
      WHERE s.id = :id AND
      s.active = false
      """)
   Optional<SubscriptionEntity> findDeactivatedById(@Param("id") long id);

   void deleteByCustomer_User_Email(final String email);

   @Query("""
      SELECT s.id
      FROM SubscriptionEntity s
      WHERE s.customer.user.email = :email
      """)
   Optional<Long> findIdByCustomerUserEmail(@Param("email") final String email);
}
