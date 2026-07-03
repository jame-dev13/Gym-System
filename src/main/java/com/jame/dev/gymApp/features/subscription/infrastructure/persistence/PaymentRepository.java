package com.jame.dev.gymApp.features.subscription.infrastructure.persistence;

import com.jame.dev.gymApp.domain.repository.CustomJpaRepository;
import com.jame.dev.gymApp.features.subscription.domain.model.PaymentEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends CustomJpaRepository<PaymentEntity, Long> {

   Optional<PaymentEntity> findByStripeSessionId(final String stripeSessionId);

   Optional<PaymentEntity> findByCustomer_User_EmailAndStatus(final String customerEmail, final PaymentStatus paymentStatus);

   boolean existsByStripeSessionIdAndStatus(final String sessionId, final PaymentStatus paymentStatus);

   List<PaymentEntity> findAllByStatusAndCreatedAtBefore(final PaymentStatus status, final Instant before);

   @NativeQuery(value = """
      SELECT p.*
      FROM payments p
      WHERE p.customer_id = :customerId AND
      (
        :search IS NULL OR TRIM(:search) = '' OR
        CAST(p.amount AS TEXT) ILIKE CONCAT('%', :search, '%') OR
        CAST(p.payment_method AS TEXT) ILIKE CONCAT('%', :search , '%') OR
        CAST(p.status AS TEXT) ILIKE CONCAT('%', :search , '%')
      )
      """,
      countQuery = """
               SELECT COUNT(*) FROM payments p
               WHERE p.customer_id = :customerId AND
            (
              :search IS NULL OR TRIM(:search) = '' OR
              CAST(p.amount AS TEXT) ILIKE CONCAT('%', :search, '%') OR
              CAST(p.payment_method AS TEXT) ILIKE CONCAT('%', :search , '%') OR
              CAST(p.status AS TEXT) ILIKE CONCAT('%', :search , '%')
            )
         """)
   Page<PaymentEntity> findAll(
      @Param("customerId") final Long customerId,
      @Param("search") final String search,
      final Pageable pageable
   );
}
