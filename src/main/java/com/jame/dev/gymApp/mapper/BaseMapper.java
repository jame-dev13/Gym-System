package com.jame.dev.gymApp.mapper;

public interface BaseMapper<E, DTO> {
   DTO toDto(final E entity);
}
