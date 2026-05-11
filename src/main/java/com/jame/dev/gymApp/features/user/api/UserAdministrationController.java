package com.jame.dev.gymApp.features.user.api;

import com.jame.dev.gymApp.features.auth.infrastructure.annotation.PublishVerifyAndNotifyUser;
import com.jame.dev.gymApp.infrastructure.annotation.Minimum;
import com.jame.dev.gymApp.infrastructure.annotation.NotNullObject;
import com.jame.dev.gymApp.infrastructure.web.BaseController;
import com.jame.dev.gymApp.features.user.infrastructure.web.UserInactiveController;
import com.jame.dev.gymApp.features.user.api.request.UserRequest;
import com.jame.dev.gymApp.features.user.api.response.UserResponse;
import com.jame.dev.gymApp.features.user.api.response.UserMinimalInfoResponse;
import com.jame.dev.gymApp.features.user.application.contract.UserService;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/v1/administration/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserAdministrationController extends BaseController<UserResponse, UserRequest> {

   private final UserInactiveController inactiveController;

   public UserAdministrationController(
      final UserService service,
      final UserInactiveController inactiveController
   ) {
      super(service, UserResponse::id);
      this.inactiveController = inactiveController;
   }

   @GetMapping
   public ResponseEntity<@NonNull Page<@NonNull UserResponse>> getUsersPage(
      @PageableDefault(
         sort = "id",
         direction = Sort.Direction.DESC) final Pageable pageable,
      @RequestParam(required = false, name = "search") String search) {
      return super.getPage(pageable, search);
   }

   @GetMapping("/inactive")
   public ResponseEntity<Page<UserMinimalInfoResponse>> getInactivePage(
      @RequestParam("page") final int page,
      @RequestParam("size") final int size
   ) {
      return this.inactiveController.getInactivePage(page, size);
   }

   @GetMapping("/{id}")
   public ResponseEntity<@NonNull UserResponse> getUser(
      @PathVariable("id") @Minimum final long id) {
      return super.getOne(id);
   }

   @PostMapping
   @PublishVerifyAndNotifyUser
   public ResponseEntity<@NonNull UserResponse> postUser(
      @RequestBody @Valid
      @NotNullObject final UserRequest userRequest) {
      return super.create(userRequest);
   }

   @PutMapping("/{id}")
   public ResponseEntity<@NonNull UserResponse> updateUser(
      @PathVariable("id")
      @Minimum final long id,
      @RequestBody
      @Valid
      @NotNullObject final UserRequest userRequest) {
      return super.update(id, userRequest);
   }

   @PatchMapping("/{id}/recover")
   public ResponseEntity<Void> recoverUser(
      @PathVariable("id")
      @Minimum long id) {
      return this.inactiveController.recover(id);
   }

   @DeleteMapping("/{id}")
   public ResponseEntity<@NonNull Void> deleteUser(
      @PathVariable("id")
      @Minimum final long id) {
      return super.delete(id);
   }

   @DeleteMapping("/{id}/hard")
   public ResponseEntity<Void> deleteUserHard(
      @PathVariable("id")
      @Minimum final long id) {
      return this.inactiveController.hardDelete(id);
   }
}
