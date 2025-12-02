package com.jame.dev.gymApp.mapper;

import com.jame.dev.gymApp.entity.PeriodEntity;
import com.jame.dev.gymApp.model.dto.out.PeriodDtoOutput;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PeriodMapper {
   PeriodDtoOutput toDto(PeriodEntity entity);
}
