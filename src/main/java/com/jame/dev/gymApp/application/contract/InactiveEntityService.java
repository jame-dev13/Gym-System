package com.jame.dev.gymApp.application.contract;

import com.jame.dev.gymApp.application.dto.PageDto;
import org.springframework.data.domain.Pageable;

public interface InactiveEntityService<DTO> extends EntityRecover<Long>, EntityRemover {
   PageDto<DTO> getInactivePage(Pageable pageable, String search);

}
