package com.jame.dev.gymApp.features.subscription.domain.repository;

import com.jame.dev.gymApp.features.subscription.domain.model.PaymentEntity;

import java.util.Optional;

public interface PaymentQueryRepository {

    Optional<PaymentEntity> findById(final long id);

    Optional<PaymentEntity> findByStripeSessionId(final String stripeSessionId);
}
