package com.jame.dev.gymApp.infrastructure.web;

import com.jame.dev.gymApp.application.contract.InactiveEntityService;
import com.jame.dev.gymApp.application.dto.PageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

@RequiredArgsConstructor
public abstract class InactiveController<DTO> {
   private final InactiveEntityService<DTO> inactiveService;

   public ResponseEntity<Page<DTO>> getInactivePage(final Pageable pageable, final String search) {
      final PageDto<DTO> pageDto = inactiveService.getInactivePage(pageable, search);
      final Page<DTO> body = new PageImpl<>(pageDto.content(), pageable, pageDto.totalElements());
      return ResponseEntity.ok(body);
   }

   public ResponseEntity<Void> recover(long id) {
      inactiveService.recover(id);
      return ResponseEntity.ok().build();
   }

   public ResponseEntity<Void> hardDelete(long id) {
      inactiveService.hardDelete(id);
      return ResponseEntity.noContent().build();
   }
}
