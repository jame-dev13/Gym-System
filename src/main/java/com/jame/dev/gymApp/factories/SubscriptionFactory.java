package com.jame.dev.gymApp.factories;

import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.entity.PeriodEntity;
import com.jame.dev.gymApp.entity.PricingEntity;
import com.jame.dev.gymApp.entity.SubscriptionEntity;
import com.jame.dev.gymApp.mapper.SubscriptionMapper;
import com.jame.dev.gymApp.model.dto.in.SubscriptionDtoInput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionFactory {
   private final SubscriptionMapper subscriptionMapper;
   private final PeriodFactory periodFactory;

   public SubscriptionEntity createFrom(
           final SubscriptionDtoInput subDto, final CustomerEntity customer,
           final PricingEntity pricing, final LocalDate startDate) {
      final List<PeriodEntity> periods = periodFactory.createPeriodsFrom(pricing, startDate);
      return subscriptionMapper.toEntity(subDto, customer, pricing, periods);
   }
}
