package com.jame.dev.gymApp.features.subscription.application.support.mapper;

import com.jame.dev.gymApp.features.subscription.application.dto.PeriodResponse;
import com.jame.dev.gymApp.features.subscription.domain.model.PeriodEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PeriodMapper {

   @Mapping(target = "periodType", source = "period")
   @Mapping(target = "periodStr", expression = "java(mapToPeriodStr(entity))")
   PeriodResponse toDto(PeriodEntity entity);

   default String mapToPeriodStr(PeriodEntity entity) {
      return entity.getStartPeriod() + " - " + entity.getEndPeriod();
   }
}
