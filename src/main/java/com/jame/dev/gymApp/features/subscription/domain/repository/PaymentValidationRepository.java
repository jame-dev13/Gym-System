package com.jame.dev.gymApp.features.subscription.domain.repository;

import com.jame.dev.gymApp.features.subscription.domain.model.PaymentStatus;

public interface PaymentValidationRepository {
   boolean existsBySessionIdAndPaymentStatus(String sessionId, PaymentStatus paymentStatus);
}
