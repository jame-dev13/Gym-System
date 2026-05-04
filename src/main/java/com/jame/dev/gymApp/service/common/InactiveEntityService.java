package com.jame.dev.gymApp.service.common;

import com.jame.dev.gymApp.model.dto.out.PageDto;
import org.springframework.data.domain.Pageable;

public interface InactiveEntityService<DTO> extends EntityRecover<Long>, EntityRemover {
   PageDto<DTO> getInactivePage(Pageable pageable);

}
