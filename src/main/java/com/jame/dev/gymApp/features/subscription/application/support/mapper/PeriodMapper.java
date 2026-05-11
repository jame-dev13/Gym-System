package com.jame.dev.gymApp.features.subscription.application.support.mapper;

import com.jame.dev.gymApp.features.subscription.domain.model.PeriodEntity;
import com.jame.dev.gymApp.features.subscription.application.dto.PeriodDtoOutput;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PeriodMapper {
   PeriodDtoOutput toDto(PeriodEntity entity);
}
