package com.jame.dev.gymApp.features.subscription.infrastructure.adapter;

import com.jame.dev.gymApp.features.subscription.domain.model.PaymentEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.PaymentStatus;
import com.jame.dev.gymApp.features.subscription.domain.repository.PaymentQueryRepository;
import com.jame.dev.gymApp.features.subscription.infrastructure.persistence.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PaymentQueryJpaRepositoryAdapter implements PaymentQueryRepository {
    private final PaymentRepository paymentRepository;

    @Override
    public Optional<PaymentEntity> findById(long id) {
        return paymentRepository.findById(id);
    }

   @Override
   public Optional<PaymentEntity> findByCustomerEmailAndStatus(String customerEmail, PaymentStatus paymentStatus) {
      return paymentRepository.findByCustomer_User_EmailAndStatus(customerEmail, paymentStatus);
   }

   @Override
    public Optional<PaymentEntity> findByStripeSessionId(String stripeSessionId) {
        return paymentRepository.findByStripeSessionId(stripeSessionId);
    }

    @Override
    public List<PaymentEntity> findAllByStatusAndCreatedAtBefore(PaymentStatus status, Instant before) {
        return paymentRepository.findAllByStatusAndCreatedAtBefore(status, before);
    }
}
