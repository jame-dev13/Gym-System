package com.jame.dev.gymApp.factories;

import com.jame.dev.gymApp.entity.PeriodEntity;
import com.jame.dev.gymApp.entity.PricingEntity;
import com.jame.dev.gymApp.shared.enums.Period;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PeriodFactory {

   public PeriodEntity createPeriodFrom(final PricingEntity pricing, final LocalDate startDate) {
      final String valueName = pricing.getMemberShipEntity().getMembership().name();
      final Optional<Period> periodOptional = Optional.of(Period.valueOf(valueName));
      final Period period = periodOptional
              .orElseThrow(() -> new IllegalArgumentException("No period value present for: " + valueName));
      return new PeriodEntity(period, startDate);
   }

   public List<PeriodEntity> createPeriodsFrom(
           final PricingEntity pricing, final LocalDate startDate) {
      return List.of(createPeriodFrom(pricing, startDate));
   }
}
