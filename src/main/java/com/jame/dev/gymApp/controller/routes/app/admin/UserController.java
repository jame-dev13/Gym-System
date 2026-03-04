package com.jame.dev.gymApp.controller.routes.app.admin;

import com.jame.dev.gymApp.aspects.annotations.Minimum;
import com.jame.dev.gymApp.aspects.annotations.NotNullObject;
import com.jame.dev.gymApp.controller.security.VerifyAdmin;
import com.jame.dev.gymApp.controller.service.BaseControllerCommon;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
import com.jame.dev.gymApp.model.dto.out.UserDtoOutput;
import com.jame.dev.gymApp.service.in.UserService;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/v1/administration/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController extends BaseControllerCommon<UserDtoOutput, UserDtoInput> {
   private final VerifyAdmin verifyAdmin;

   public UserController(
           final UserService service,
           final VerifyAdmin verifyAdmin) {
      super(service, UserDtoOutput::id);
      this.verifyAdmin = verifyAdmin;
   }

   @GetMapping
   public ResponseEntity<@NonNull Page<@NonNull UserDtoOutput>> getUsers(
           @RequestParam("page") final int page,
           @RequestParam("size") final int size) {
      return super.getPage(page, size);
   }

   @GetMapping("/{id}")
   public ResponseEntity<@NonNull UserDtoOutput> getUser(
           @PathVariable("id") @Minimum final long id) {
      return super.getOne(id);
   }

   @PostMapping
   public ResponseEntity<@NonNull UserDtoOutput> postUser(
           @RequestBody @Valid @NotNullObject final UserDtoInput userDtoInput) {
      var response = super.create(userDtoInput);
      verifyAdmin.verifyAndApproveAdmin(userDtoInput);
      return response;
   }

   @PutMapping("/{id}")
   public ResponseEntity<@NonNull UserDtoOutput> updateUser(
           @PathVariable("id")
           @Minimum final long id,
           @RequestBody
           @Valid
           @NotNullObject final UserDtoInput userDtoInput) {
      return super.update(id, userDtoInput);
   }

   @DeleteMapping("/{id}")
   public ResponseEntity<@NonNull Void> deleteUser(
           @PathVariable("id")
           @Minimum final long id) {
      return super.delete(id);
   }
}
