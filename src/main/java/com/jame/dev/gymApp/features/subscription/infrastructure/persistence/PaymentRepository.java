package com.jame.dev.gymApp.features.subscription.infrastructure.persistence;

import com.jame.dev.gymApp.domain.repository.CustomJpaRepository;
import com.jame.dev.gymApp.features.subscription.domain.model.PaymentEntity;

import java.util.Optional;

public interface PaymentRepository extends CustomJpaRepository<PaymentEntity, Long> {

    Optional<PaymentEntity> findByStripeSessionId(final String stripeSessionId);

    boolean existsByStripeSessionId(final String sessionId);
}
