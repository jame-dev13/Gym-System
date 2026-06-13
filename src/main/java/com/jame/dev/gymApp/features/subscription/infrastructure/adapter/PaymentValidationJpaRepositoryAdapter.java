package com.jame.dev.gymApp.features.subscription.infrastructure.adapter;

import com.jame.dev.gymApp.features.subscription.domain.repository.PaymentValidationRepository;
import com.jame.dev.gymApp.features.subscription.infrastructure.persistence.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentValidationJpaRepositoryAdapter implements PaymentValidationRepository {
   private final PaymentRepository paymentRepository;

   @Override
   public boolean existsBySessionId(String sessionId) {
      return paymentRepository.existsByStripeSessionId(sessionId);
   }

}
