package com.jame.dev.gymApp.features.subscription.application.contract;

import com.jame.dev.gymApp.application.support.factories.Factory;
import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.features.subscription.application.dto.SubscriptionFactoryDtoInput;
import com.jame.dev.gymApp.features.subscription.domain.model.PeriodEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.PricingEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;

import java.util.List;

public interface SubscriptionFactory extends Factory<
   SubscriptionEntity, SubscriptionResponse, SubscriptionFactoryDtoInput> {

   default SubscriptionEntity from(CustomerEntity customerEntity, PricingEntity pricingEntity, List<PeriodEntity> subscriptionPeriods) {
      return SubscriptionEntity.builder()
         .customer(customerEntity)
         .pricing(pricingEntity)
         .subscriptionPeriods(subscriptionPeriods)
         .finished(false)
         .paid(false)
         .build();
   }
}
