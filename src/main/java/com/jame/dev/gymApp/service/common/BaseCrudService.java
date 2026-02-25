package com.jame.dev.gymApp.service.common;

import com.jame.dev.gymApp.model.dto.out.PageDto;
import lombok.NonNull;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface  BaseCrudService<DTO_OUT, DTO_IN, ID> {
   PageDto<@NonNull DTO_OUT> getPage(@NonNull final Pageable pageable);

   DTO_OUT save(@NonNull final DTO_IN dtoIn);

   Optional<DTO_OUT> getById(@NonNull final ID id);

   DTO_OUT update(final ID id, @NonNull final DTO_IN dtoIn);

   void softDelete(@NonNull final ID id);
}
