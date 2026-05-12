package com.jame.dev.gymApp.application.support.factories;

import com.jame.dev.gymApp.application.dto.PageDto;
import org.springframework.data.domain.Page;

public interface Factory<E, DTO_OUT, DTO_IN> {
   PageDto<DTO_OUT> createPageFrom(final Page<E> page);
   DTO_OUT createFromEntity(final E entity);
   E createFromInput(DTO_IN input);
}
