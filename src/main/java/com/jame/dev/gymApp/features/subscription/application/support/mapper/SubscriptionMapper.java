package com.jame.dev.gymApp.features.subscription.application.support.mapper;

import com.jame.dev.gymApp.application.support.mapper.BaseMapper;
import com.jame.dev.gymApp.features.customer.application.support.mapper.CustomerMapper;
import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.features.subscription.domain.model.*;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",
   uses = {CustomerMapper.class, PeriodMapper.class},
   injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface SubscriptionMapper extends BaseMapper<SubscriptionEntity, SubscriptionResponse> {

   @Override
   @Mapping(source = "customer.user.email", target = "customerEmail")
   @Mapping(source = "pricing.memberShipEntity.membership", target = "membership")
   @Mapping(source = "pricing.price", target = "price")
   SubscriptionResponse toDto(SubscriptionEntity entity);

   default SubscriptionEntity toEntity(
      CustomerEntity customerEntity,
      PricingEntity pricingEntity,
      List<PeriodEntity> periods) {
      return SubscriptionEntity.builder()
         .customer(customerEntity)
         .pricing(pricingEntity)
         .subscriptionPeriods(periods)
         .status(SubscriptionStatus.NOT_PAID)
         .build();
   }
}
