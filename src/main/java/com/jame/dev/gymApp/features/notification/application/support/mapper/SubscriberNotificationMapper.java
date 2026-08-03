package com.jame.dev.gymApp.features.notification.application.support.mapper;

import com.jame.dev.gymApp.application.support.mapper.BaseMapper;
import com.jame.dev.gymApp.features.notification.api.response.SubscriberNotificationResponse;
import com.jame.dev.gymApp.features.notification.application.dto.SubscriberNotificationFactoryDtoInput;
import com.jame.dev.gymApp.features.notification.domain.model.SubscriberNotificationEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface SubscriberNotificationMapper extends BaseMapper<SubscriberNotificationEntity, SubscriberNotificationResponse> {

   @Override
   @Mapping(target = "uuid", source = "entity.id")
   @Mapping(target = "rangeDaysNotification", source = "entity.rangeNotificationDays")
   @Mapping(target = "nextNotificationDate", source = "entity.nextNotificationDate", dateFormat = "EEEE, MMMM dd, yyyy")
   SubscriberNotificationResponse toDto(SubscriberNotificationEntity entity);

   default SubscriberNotificationEntity toEntity(SubscriberNotificationFactoryDtoInput input) {
      final SubscriptionEntity subscription = input.subscription();
      final var endingPeriod = subscription.getSubscriptionPeriods().getLast().getEndPeriod();
      return SubscriberNotificationEntity.builder()
         .subscription(input.subscription())
         .rangeNotificationDays(input.rangeDays())
         .nextNotificationDate(LocalDateTime.from(endingPeriod).minusDays(input.rangeDays()))
         .build();
   }
}
