package com.jame.dev.gymApp.features.subscription.application.support.mapper;

import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.PeriodEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.PricingEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionRequest;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.application.support.mapper.BaseMapper;
import com.jame.dev.gymApp.features.customer.application.support.mapper.CustomerMapper;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",
   uses = {CustomerMapper.class, PeriodMapper.class},
   injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface SubscriptionMapper extends BaseMapper<SubscriptionEntity, SubscriptionResponse> {

   @Override
   @Mapping(source = "id", target = "id")
   @Mapping(source = "customer", target = "customer")
   @Mapping(source = "pricing.memberShipEntity.membership", target = "membership")
   @Mapping(source = "pricing.price", target = "price")
   @Mapping(target = "periods", source = "subscriptionPeriods")
   SubscriptionResponse toDto(SubscriptionEntity entity);

   default SubscriptionEntity toEntity(SubscriptionRequest dto,
                                       CustomerEntity customerEntity,
                                       PricingEntity pricingEntity,
                                       List<PeriodEntity> periods) {
      return SubscriptionEntity.builder()
         .customer(customerEntity)
         .pricing(pricingEntity)
         .subscriptionPeriods(periods)
         .finished(false)
         .build();
   }
}
