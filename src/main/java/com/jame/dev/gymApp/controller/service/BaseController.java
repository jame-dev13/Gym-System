package com.jame.dev.gymApp.controller.service;

import com.jame.dev.gymApp.aspects.annotations.constraints.Minimum;
import com.jame.dev.gymApp.aspects.annotations.constraints.NotNullObject;
import com.jame.dev.gymApp.model.dto.out.PageDto;
import com.jame.dev.gymApp.service.common.BaseService;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.function.Function;

@Validated
public abstract class BaseController<OUT, IN> {

   protected final BaseService<OUT, IN> service;
   private final Function<OUT, Long> idExtractor;

   protected BaseController(
      BaseService<OUT, IN> service,
      Function<OUT, Long> idExtractor) {
      this.service = service;
      this.idExtractor = idExtractor;
   }

   public ResponseEntity<@NonNull Page<@NonNull OUT>> getPage(
           final int page,
           final int size) {
      final Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
      final PageDto<@NonNull OUT> dtoPage = service.getPage(pageable);
      final Page<OUT> response = new PageImpl<>(dtoPage.content(), pageable, dtoPage.totalElements());
      return ResponseEntity.ok(response);
   }

   protected ResponseEntity<@NonNull OUT> getOne(final long id) {
      final OUT body = service.getById(id);
      return ResponseEntity.ok(body);
   }

   protected ResponseEntity<@NonNull OUT> create(final IN dto) {
      final OUT response = service.save(dto);
      final URI created = ServletUriComponentsBuilder
              .fromCurrentRequest()
              .path("/{id}")
              .buildAndExpand(idExtractor.apply(response))
              .toUri();
      return ResponseEntity.created(created)
              .body(response);
   }

   protected ResponseEntity<@NonNull OUT> update(
           @Minimum
           final long id,
           @Valid
           @NotNullObject(message = "Payload must not be null") final IN dto) {
      final OUT response = service.update(id, dto);
      return ResponseEntity.ok(response);
   }

   protected ResponseEntity<Void> delete(
           @Minimum
           final long id) {
      service.softDelete(id);
      return ResponseEntity.noContent().build();
   }
}
