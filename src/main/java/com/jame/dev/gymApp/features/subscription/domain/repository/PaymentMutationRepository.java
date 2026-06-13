package com.jame.dev.gymApp.features.subscription.domain.repository;

import com.jame.dev.gymApp.features.subscription.domain.model.PaymentEntity;

public interface PaymentMutationRepository {

    PaymentEntity save(final PaymentEntity paymentEntity);

    void deleteById(long id);
}
