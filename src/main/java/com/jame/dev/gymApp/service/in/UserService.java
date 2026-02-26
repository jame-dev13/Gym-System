package com.jame.dev.gymApp.service.in;

import com.jame.dev.gymApp.aspects.annotations.EmailValid;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
import com.jame.dev.gymApp.model.dto.out.UserDtoOutput;
import com.jame.dev.gymApp.service.common.BaseCrudService;

import java.util.Optional;

public interface UserService extends
        BaseCrudService<UserDtoOutput, UserDtoInput> {
   Optional<UserEntity> getUserByEmail(@EmailValid final String email);
}
