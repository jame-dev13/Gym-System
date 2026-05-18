package com.jame.dev.gymApp.mapper;

import com.jame.dev.gymApp.features.subscription.application.dto.PeriodDtoOutput;
import com.jame.dev.gymApp.features.subscription.application.support.mapper.PeriodMapper;
import com.jame.dev.gymApp.features.subscription.application.support.mapper.PeriodMapperImpl;
import com.jame.dev.gymApp.features.subscription.domain.model.Period;
import com.jame.dev.gymApp.features.subscription.domain.model.PeriodEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class PeriodMapperTest {
   private final PeriodMapper periodMapper = new PeriodMapperImpl();

   @Test
   @DisplayName("To dto")
   void toDto(){
      PeriodEntity periodEntity = PeriodEntity.builder()
              .period(Period.MONTHLY)
              .startPeriod(LocalDate.now())
              .build();
      PeriodDtoOutput periodDtoOutput = periodMapper.toDto(periodEntity);
      Assertions.assertNotNull(periodDtoOutput, "Should not be null.");
   }
}
