package com.jame.dev.gymApp.features.subscription.infrastructure.adapter;

import com.jame.dev.gymApp.features.subscription.domain.model.PaymentEntity;
import com.jame.dev.gymApp.features.subscription.domain.repository.PaymentMutationRepository;
import com.jame.dev.gymApp.features.subscription.infrastructure.persistence.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentMutationJpaRepositoryAdapter implements PaymentMutationRepository {
    private final PaymentRepository paymentRepository;

    @Override
    public PaymentEntity save(PaymentEntity paymentEntity) {
        return paymentRepository.saveAndFlush(paymentEntity);
    }

    @Override
    public void deleteById(long id) {
        paymentRepository.deleteById(id);
    }
}
