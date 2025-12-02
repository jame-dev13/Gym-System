package com.jame.dev.gymApp.mapper;

import com.jame.dev.gymApp.entity.PeriodEntity;
import com.jame.dev.gymApp.model.dto.out.PeriodDtoOutput;
import com.jame.dev.gymApp.shared.enums.Period;
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
