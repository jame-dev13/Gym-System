package com.jame.dev.gymApp.factories;

import com.jame.dev.gymApp.mapper.BaseMapper;
import com.jame.dev.gymApp.model.dto.out.PageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

@RequiredArgsConstructor
public class PageDtoFactory<E, DTO> {
   private final BaseMapper<E, DTO> mapper;

   public PageDto<DTO> createPageDtoFrom(final Page<E> page) {
      final var content = page.getContent().stream().map(mapper::toDto).toList();
      return new PageDto<>(
              content,
              page.getNumber(),
              page.getSize(),
              page.getTotalElements(),
              page.getSort().toString(),
              page.getSort().isSorted() ? "ASC" : "DESC"
      );
   }
}
