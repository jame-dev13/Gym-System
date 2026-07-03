package com.jame.dev.gymApp.features.subscription.infrastructure.payment.mapper;

import com.jame.dev.gymApp.features.subscription.api.response.PaymentResponse;
import com.jame.dev.gymApp.features.subscription.domain.model.PaymentEntity;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PaymentMapper {

   @Mapping(target = "createdAt", expression = "java(mapInstantToHumanReadable(paymentEntity))")
   PaymentResponse toResponse(PaymentEntity paymentEntity);

   default String mapInstantToHumanReadable(PaymentEntity paymentEntity) {
      if (Objects.isNull(paymentEntity.getCreatedAt())) return "No Timestamp";
      final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE MMMM, d yyyy HH:mm:ss")
         .withZone(ZoneId.systemDefault())
         .withLocale(Locale.US);
      return formatter.format(paymentEntity.getCreatedAt());
   }
}
