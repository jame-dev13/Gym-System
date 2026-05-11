package com.jame.dev.gymApp.infrastructure.web;

import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.application.contract.InactiveEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;

@RequiredArgsConstructor
public abstract class InactiveController<DTO> {
   private final InactiveEntityService<DTO> inactiveService;

   public ResponseEntity<Page<DTO>> getInactivePage(int page, int size) {
      final Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));
      final PageDto<DTO> pageDto = inactiveService.getInactivePage(pageable);
      final Page<DTO> body = new PageImpl<>(pageDto.content(), pageable, pageDto.totalElements());
      return ResponseEntity.ok(body);
   }

   public ResponseEntity<Void> recover(long id) {
      inactiveService.recover(id);
      return ResponseEntity.ok().build();
   }

   public ResponseEntity<Void> hardDelete(long id){
      inactiveService.hardDelete(id);
      return ResponseEntity.noContent().build();
   }
}
