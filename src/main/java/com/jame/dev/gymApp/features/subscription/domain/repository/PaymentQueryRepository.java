package com.jame.dev.gymApp.features.subscription.domain.repository;

import com.jame.dev.gymApp.features.subscription.domain.model.PaymentEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PaymentQueryRepository {

   Page<PaymentEntity> findPaymentPage(final Long customerId, final Pageable pageable);

    Optional<PaymentEntity> findById(final long id);

    Optional<PaymentEntity> findByCustomerEmailAndStatus(final String customerEmail, final PaymentStatus paymentStatus);

    Optional<PaymentEntity> findByStripeSessionId(final String stripeSessionId);

    List<PaymentEntity> findAllByStatusAndCreatedAtBefore(final PaymentStatus status, final Instant before);
}
