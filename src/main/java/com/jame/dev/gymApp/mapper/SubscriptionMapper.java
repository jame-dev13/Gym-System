package com.jame.dev.gymApp.mapper;

import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.entity.PeriodEntity;
import com.jame.dev.gymApp.entity.PricingEntity;
import com.jame.dev.gymApp.entity.SubscriptionEntity;
import com.jame.dev.gymApp.model.dto.in.SubscriptionDtoInput;
import com.jame.dev.gymApp.model.dto.out.SubscriptionDtoOutput;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",
   uses = {CustomerMapper.class, PeriodMapper.class},
   injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface SubscriptionMapper extends BaseMapper<SubscriptionEntity, SubscriptionDtoOutput> {

   @Override
   @Mapping(source = "id", target = "id")
   @Mapping(source = "customer", target = "customer")
   @Mapping(source = "pricing.memberShipEntity.membership", target = "membership")
   @Mapping(source = "pricing.price", target = "price")
   @Mapping(target = "periods", source = "subscriptionPeriods")
   SubscriptionDtoOutput toDto(SubscriptionEntity entity);

   default SubscriptionEntity toEntity(SubscriptionDtoInput dto,
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
