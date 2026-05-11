package com.jame.dev.gymApp.application.support.mapper;

public interface BaseMapper<E, DTO> {
   DTO toDto(final E entity);
}
