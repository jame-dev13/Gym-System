package com.jame.dev.gymApp.features.subscription.domain.repository;

public interface PaymentValidationRepository {
   boolean existsBySessionId(String sessionId);
}
