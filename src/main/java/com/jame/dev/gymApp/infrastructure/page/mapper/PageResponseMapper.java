package com.jame.dev.gymApp.infrastructure.page.mapper;

import com.jame.dev.gymApp.application.dto.PageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class PageResponseMapper {

   public final <T> Page<T> from(final PageDto<T> pageDto, final Pageable pageable) {
      return new PageImpl<T>(pageDto.content(), pageable, pageDto.totalElements());
   }
}
