package com.jame.dev.gymApp.features.notification.application.support.factory;

import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.application.support.factories.PageDtoFactory;
import com.jame.dev.gymApp.features.notification.api.response.SubscriberNotificationResponse;
import com.jame.dev.gymApp.features.notification.application.contract.SubscriberNotificationFactory;
import com.jame.dev.gymApp.features.notification.application.dto.SubscriberNotificationFactoryDtoInput;
import com.jame.dev.gymApp.features.notification.application.support.mapper.SubscriberNotificationMapper;
import com.jame.dev.gymApp.features.notification.domain.model.SubscriberNotificationEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscriberNotificationApplicationFactory implements SubscriberNotificationFactory {
   private final SubscriberNotificationMapper subscriberNotificationMapper;
   private final PageDtoFactory<SubscriberNotificationEntity, SubscriberNotificationResponse> pageSubscriberNotificationFactory;

   @Override
   public PageDto<SubscriberNotificationResponse> createPageFrom(Page<SubscriberNotificationEntity> page) {
      return pageSubscriberNotificationFactory.createPageDtoFrom(page);
   }

   @Override
   public SubscriberNotificationResponse createFromEntity(SubscriberNotificationEntity entity) {
      return subscriberNotificationMapper.toDto(entity);
   }

   @Override
   public SubscriberNotificationEntity createFromInput(SubscriberNotificationFactoryDtoInput input) {
      return subscriberNotificationMapper.toEntity(input);
   }
}
