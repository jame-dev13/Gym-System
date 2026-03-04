package com.jame.dev.gymApp.controller.service;

import com.jame.dev.gymApp.aspects.annotations.Minimum;
import com.jame.dev.gymApp.aspects.annotations.NotNullObject;
import com.jame.dev.gymApp.exception.EntityNotFoundException;
import com.jame.dev.gymApp.model.dto.out.PageDto;
import com.jame.dev.gymApp.service.common.ServiceComplex;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.function.Function;

@RequiredArgsConstructor
@Validated
public abstract class ControllerComplex<DTO_OUT, DTO_IN, E> {
   private final ServiceComplex<DTO_OUT, DTO_IN, E> service;
   private final Function<DTO_OUT, Long> idExtractor;

   public ResponseEntity<@NonNull Page<@NonNull DTO_OUT>> getPage(
           final int page,
           final int size) {
      final Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
      final PageDto<@NonNull DTO_OUT> dtoPage = service.getPage(pageable);
      final Page<DTO_OUT> response = new PageImpl<>(dtoPage.content(), pageable, dtoPage.totalElements());
      return ResponseEntity.ok(response);
   }

   protected ResponseEntity<@NonNull DTO_OUT> getOne(final long id) {
      final DTO_OUT response = service.getById(id)
              .orElseThrow(() -> new EntityNotFoundException("Not found id: " + id));
      return ResponseEntity.ok(response);
   }

   protected ResponseEntity<@NonNull DTO_OUT> create(final DTO_IN dto) {
      final DTO_OUT response = service.save(dto);
      final URI created = ServletUriComponentsBuilder
              .fromCurrentRequest()
              .path("/{id}")
              .buildAndExpand(idExtractor.apply(response))
              .toUri();
      return ResponseEntity.created(created)
              .body(response);
   }

   protected ResponseEntity<@NonNull DTO_OUT> update(
           @Minimum
           final long id,
           @Valid
           @NotNullObject(message = "Payload must not be null") final DTO_IN dto) {
      final DTO_OUT response = service.update(id, dto);
      return ResponseEntity.ok(response);
   }

   protected ResponseEntity<Void> delete(
           @Minimum
           final long id) {
      service.softDelete(id);
      return ResponseEntity.noContent().build();
   }

   protected ResponseEntity<@NonNull DTO_OUT> patch(
           @Positive(message = "Value must be positive integer and non cero.")
           final long id) {
      final DTO_OUT response = service.patch(id);
      return ResponseEntity.ok(response);
   }
   protected ResponseEntity<@NonNull DTO_OUT> put(
           @Positive(message = "Value must be positive integer and non cero.")
           final long id,
           @Valid
           @NotNullObject(message = "Payload must not be null.") final DTO_IN dto) {
      final DTO_OUT response = service.put(id, dto);
      return ResponseEntity.ok(response);
   }
}
