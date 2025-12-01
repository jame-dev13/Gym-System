package com.jame.dev.gymApp.mapper;

import com.jame.dev.gymApp.entity.PeriodEntity;
import com.jame.dev.gymApp.model.dto.out.CustomerDtoOutput;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PeriodMapper {
   CustomerDtoOutput toDto(PeriodEntity entity);
}
