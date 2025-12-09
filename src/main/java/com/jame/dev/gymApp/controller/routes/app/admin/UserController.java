package com.jame.dev.gymApp.controller.routes.app.admin;

import com.jame.dev.gymApp.cache.service.AppCacheService;
import com.jame.dev.gymApp.controller.service.BaseController;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.mapper.BaseMapper;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
import com.jame.dev.gymApp.model.dto.out.UserDtoOutput;
import com.jame.dev.gymApp.service.common.CRUDService;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users")
public class UserController extends BaseController<UserEntity, UserDtoInput, UserDtoOutput> {

   protected UserController(CRUDService<UserEntity, UserDtoInput> service,
                            BaseMapper<UserEntity, UserDtoOutput> mapper,
                            AppCacheService<UserDtoOutput> cache) {
      super(service, mapper, cache, "users");
   }

   @GetMapping
   public ResponseEntity<@NonNull Page<@NonNull UserDtoOutput>> getUsers(
           @RequestParam("page") final int page,
           @RequestParam("size") final int size) {
      return super.getPage(page, size);
   }

   @GetMapping("/{id}")
   public ResponseEntity<@NonNull UserDtoOutput> getUser(@PathVariable("id") final long id){
      return super.getOne(id);
   }

   @PostMapping
   public ResponseEntity<@NonNull UserDtoOutput> postUser(@RequestBody final UserDtoInput userDtoInput) {
      return super.create(userDtoInput, "/admin/users");
   }

   @PutMapping("/{id}")
   public ResponseEntity<@NonNull UserDtoOutput> putUser(@PathVariable final long id, @RequestBody final UserDtoInput userDtoInput) {
      return super.put(id, userDtoInput);
   }

   @DeleteMapping("/{id}")
   public ResponseEntity<@NonNull Void> deleteUser(@RequestParam final long id) {
      return super.delete(id);
   }
}
