package com.jame.dev.gymApp.service.in;

import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
import com.jame.dev.gymApp.model.dto.out.UserDtoOutput;
import com.jame.dev.gymApp.service.common.BaseCrudService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Optional;

public interface UserService extends
        BaseCrudService<UserDtoOutput, UserDtoInput, Long> {
   Optional<UserEntity> getUserByEmail(@NotBlank @Email final String email);
}
