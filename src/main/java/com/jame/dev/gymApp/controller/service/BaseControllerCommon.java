package com.jame.dev.gymApp.controller.service;

import com.jame.dev.gymApp.exception.EntityNotFoundException;
import com.jame.dev.gymApp.model.dto.out.PageDto;
import com.jame.dev.gymApp.service.common.BaseCrudService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.function.Function;

@RequiredArgsConstructor
public abstract class BaseControllerCommon<DTO_OUT, DTO_IN> {

   protected final BaseCrudService<DTO_OUT, DTO_IN, Long> service;
   private final Function<DTO_OUT, Long> idExtractor;

   protected ResponseEntity<@NonNull Page<@NonNull DTO_OUT>> getPage(int page, int size) {
      final Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
      final PageDto<@NonNull DTO_OUT> dtoPage = service.getPage(pageable);
      final Page<DTO_OUT> response = new PageImpl<>(dtoPage.content(), pageable, dtoPage.totalElements());
      return ResponseEntity.ok(response);
   }

   protected ResponseEntity<@NonNull DTO_OUT> getOne(long id) {
      final DTO_OUT response = service.getById(id)
              .orElseThrow(() -> new EntityNotFoundException("Not found id: " + id));
      return ResponseEntity.ok(response);
   }

   protected ResponseEntity<@NonNull DTO_OUT> create(@NonNull final DTO_IN dto, final String location) {
      final DTO_OUT response = service.save(dto);
      final URI created = URI.create(location + '/' + idExtractor.apply(response));
      return ResponseEntity.created(created)
              .body(response);
   }

   protected ResponseEntity<@NonNull DTO_OUT> update(final long id, @NonNull final DTO_IN dto) {
      final DTO_OUT response = service.update(id, dto);
      return ResponseEntity.ok(response);
   }

   protected ResponseEntity<Void> delete(long id) {
      service.softDelete(id);
      return ResponseEntity.noContent().build();
   }
}
