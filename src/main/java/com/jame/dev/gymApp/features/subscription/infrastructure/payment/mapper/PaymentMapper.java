package com.jame.dev.gymApp.features.subscription.infrastructure.payment.mapper;

import com.jame.dev.gymApp.features.subscription.api.response.PaymentResponse;
import com.jame.dev.gymApp.features.subscription.domain.model.PaymentEntity;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PaymentMapper {

   PaymentResponse toResponse(PaymentEntity paymentEntity);

}
