package com.jame.dev.gymApp.service.common;

import com.jame.dev.gymApp.aspects.annotations.constraints.Minimum;
import com.jame.dev.gymApp.aspects.annotations.constraints.NotNullObject;
import com.jame.dev.gymApp.aspects.annotations.constraints.SortPropertyValid;
import com.jame.dev.gymApp.model.dto.out.PageDto;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.springframework.data.domain.Pageable;

public interface BaseService<DTO_OUT, DTO_IN> {
   PageDto<@NonNull DTO_OUT> getPage(@SortPropertyValid final Pageable pageable, final String search);

   DTO_OUT save(@NotNullObject @Valid final DTO_IN dtoIn);

   DTO_OUT getById(@Minimum final long id);

   DTO_OUT update(@Minimum final long id, @NotNullObject @Valid final DTO_IN dtoIn);

   void softDelete(@Minimum final long id);
}
