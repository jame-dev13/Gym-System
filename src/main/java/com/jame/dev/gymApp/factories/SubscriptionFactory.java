package com.jame.dev.gymApp.factories;

import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.entity.PeriodEntity;
import com.jame.dev.gymApp.entity.PricingEntity;
import com.jame.dev.gymApp.entity.SubscriptionEntity;
import com.jame.dev.gymApp.factories.in.Factory;
import com.jame.dev.gymApp.mapper.SubscriptionMapper;
import com.jame.dev.gymApp.model.dto.in.SubscriptionDtoInput;
import com.jame.dev.gymApp.model.dto.in.SubscriptionFactoryDtoInput;
import com.jame.dev.gymApp.model.dto.out.PageDto;
import com.jame.dev.gymApp.model.dto.out.SubscriptionDtoOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionFactory implements Factory<SubscriptionEntity, SubscriptionDtoOutput, SubscriptionFactoryDtoInput> {
   private final SubscriptionMapper subscriptionMapper;
   private final PeriodFactory periodFactory;
   private final PageDtoFactory<SubscriptionEntity, SubscriptionDtoOutput> pageSubscriptionFactory;

   @Override
   public PageDto<SubscriptionDtoOutput> createPageFrom(final Page<SubscriptionEntity> page){
      return pageSubscriptionFactory.createPageDtoFrom(page);
   }

   @Override
   public SubscriptionDtoOutput createFromEntity(SubscriptionEntity entity) {
      return subscriptionMapper.toDto(entity);
   }

   @Override
   public SubscriptionEntity createFromInput(SubscriptionFactoryDtoInput input) {
      final List<PeriodEntity> periods = periodFactory.createPeriodsFrom(input.pricing(), input.startDate());
      final SubscriptionEntity subscriptionEntity =  subscriptionMapper.toEntity(
              input.subDto(), input.customer(), input.pricing(), periods
      );
      subscriptionEntity.setCreatedAt(Instant.now());
      return subscriptionEntity;
   }

   public SubscriptionEntity createFrom(
           final SubscriptionDtoInput subDto, final CustomerEntity customer,
           final PricingEntity pricing, final LocalDate startDate) {
      final List<PeriodEntity> periods = periodFactory.createPeriodsFrom(pricing, startDate);
      final SubscriptionEntity subscriptionEntity =  subscriptionMapper.toEntity(subDto, customer, pricing, periods);
      subscriptionEntity.setCreatedAt(Instant.now());
      return subscriptionEntity;
   }
}
