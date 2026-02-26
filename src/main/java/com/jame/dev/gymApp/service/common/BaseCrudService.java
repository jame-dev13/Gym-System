package com.jame.dev.gymApp.service.common;

import com.jame.dev.gymApp.aspects.annotations.Minimum;
import com.jame.dev.gymApp.aspects.annotations.NotNullObject;
import com.jame.dev.gymApp.model.dto.out.PageDto;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BaseCrudService<DTO_OUT, DTO_IN> {
   PageDto<@NonNull DTO_OUT> getPage(@NotNullObject @Valid final Pageable pageable);

   DTO_OUT save(@NotNullObject @Valid final DTO_IN dtoIn);

   Optional<DTO_OUT> getById(@Minimum final long id);

   DTO_OUT update(@Minimum final long id, @NotNullObject @Valid final DTO_IN dtoIn);

   void softDelete(@Minimum final long id);
}
