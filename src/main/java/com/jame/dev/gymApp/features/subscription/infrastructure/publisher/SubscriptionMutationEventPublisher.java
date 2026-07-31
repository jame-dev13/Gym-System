package com.jame.dev.gymApp.features.subscription.infrastructure.publisher;

import com.jame.dev.gymApp.features.subscription.domain.event.SubscriptionFinalizedEvent;
import com.jame.dev.gymApp.features.subscription.domain.event.SubscriptionMutationEvent;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class SubscriptionMutationEventPublisher {

   private final ApplicationEventPublisher applicationEventPublisher;
   private final Function<SubscriptionEntity, LocalDate> getEndingPeriod =
      subscription -> subscription.getSubscriptionPeriods().getLast().getEndPeriod();

   public void publishSubscriptionMutated(SubscriptionEntity subscriptionEntity) {
      applicationEventPublisher.publishEvent(new SubscriptionMutationEvent(
         subscriptionEntity.getId(),
         getEndingPeriod.apply(subscriptionEntity)
      ));
   }

   public void publishSubscriptionFinalized(SubscriptionEntity subscriptionEntity) {
      applicationEventPublisher.publishEvent(new SubscriptionFinalizedEvent(
         subscriptionEntity.getId(),
         getEndingPeriod.apply(subscriptionEntity)
      ));
   }
}
