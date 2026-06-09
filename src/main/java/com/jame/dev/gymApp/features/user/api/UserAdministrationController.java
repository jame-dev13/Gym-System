package com.jame.dev.gymApp.features.user.api;

import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.features.auth.infrastructure.annotation.PublishVerifyAndNotifyUser;
import com.jame.dev.gymApp.features.user.api.request.UserRequest;
import com.jame.dev.gymApp.features.user.api.response.UserMinimalInfoResponse;
import com.jame.dev.gymApp.features.user.api.response.UserResponse;
import com.jame.dev.gymApp.features.user.application.usecases.mutation.*;
import com.jame.dev.gymApp.features.user.application.usecases.query.GetByIdUserUseCase;
import com.jame.dev.gymApp.features.user.application.usecases.query.GetPageUserUseCase;
import com.jame.dev.gymApp.infrastructure.annotation.Minimum;
import com.jame.dev.gymApp.infrastructure.annotation.NotNullObject;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/app/v1/administration/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Validated
public class UserAdministrationController {
   private final GetPageUserUseCase getPageUserUseCase;
   private final GetByIdUserUseCase getByIdUserUseCase;
   private final CreateUserUseCase createUserUseCase;
   private final UpdateUserUseCase updateUserUseCase;
   private final ReActivateUserByIdUseCase reActivateUserByIdUseCase;
   private final SoftDeleteUserByIdUseCase softDeleteUserByIdUseCase;
   private final HardDeleteUserByIdUseCase hardDeleteUserByIdUseCase;

   @GetMapping
   public ResponseEntity<Page<UserResponse>> getUsersPage(
      @PageableDefault(
         sort = "id",
         direction = Sort.Direction.DESC
      ) final Pageable pageable,
      @RequestParam(required = false, name = "search") String search) {
      final PageDto<UserResponse> pageDto = getPageUserUseCase.getPage(pageable, search);
      final Page<UserResponse> page = new PageImpl<>(pageDto.content(), pageable, pageDto.totalElements());
      return ResponseEntity.ok(page);
   }

   @GetMapping("/inactive")
   public ResponseEntity<Page<UserMinimalInfoResponse>> getInactivePage(
      @PageableDefault(
         sort = "id",
         direction = Sort.Direction.DESC
      ) Pageable pageable,
      @RequestParam(required = false, name = "search") String search
   ) {
      final PageDto<UserMinimalInfoResponse> pageDto = getPageUserUseCase.getInactivePage(pageable, search);
      final Page<UserMinimalInfoResponse> page = new PageImpl<>(pageDto.content(), pageable, pageDto.totalElements());
      return ResponseEntity.ok(page);
   }

   @GetMapping("/{id}")
   public ResponseEntity<UserResponse> getUser(
      @PathVariable("id") @Minimum final long id) {
      return ResponseEntity.ok(getByIdUserUseCase.getById(id));
   }

   @PostMapping
   @PublishVerifyAndNotifyUser
   public ResponseEntity<UserResponse> postUser(
      @RequestBody @Valid
      @NotNullObject final UserRequest userRequest) {
      final UserResponse userResponse = createUserUseCase.create(userRequest);
      final URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
         .path("/{id}")
         .buildAndExpand(userResponse.id())
         .toUri();
      return ResponseEntity.created(uri)
         .body(userResponse);
   }

   @PutMapping("/{id}")
   public ResponseEntity<UserResponse> updateUser(
      @PathVariable("id")
      @Minimum final long id,
      @RequestBody
      @Valid
      @NotNullObject final UserRequest userRequest) {
      return ResponseEntity.ok(updateUserUseCase.update(id, userRequest));
   }

   @PatchMapping("/{id}/recover")
   public ResponseEntity<Void> recoverUser(
      @PathVariable("id")
      @Minimum long id) {
      reActivateUserByIdUseCase.reActivateById(id);
      return ResponseEntity.ok().build();
   }

   @DeleteMapping("/{id}")
   public ResponseEntity<Void> deleteUser(
      @PathVariable("id")
      @Minimum final long id) {
      softDeleteUserByIdUseCase.softDeleteById(id);
      return ResponseEntity.noContent().build();
   }

   @DeleteMapping("/{id}/hard")
   public ResponseEntity<Void> deleteUserHard(
      @PathVariable("id")
      @Minimum final long id) {
      hardDeleteUserByIdUseCase.hardDeleteById(id);
      return ResponseEntity.noContent().build();
   }
}
