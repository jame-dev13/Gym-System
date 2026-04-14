package com.jame.dev.gymApp.service.in;

import com.jame.dev.gymApp.aspects.annotations.constraints.EmailValid;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
import com.jame.dev.gymApp.model.dto.out.UserDtoOutput;
import com.jame.dev.gymApp.service.common.BaseService;

import java.util.Optional;

public interface UserService extends
   BaseService<UserDtoOutput, UserDtoInput> {
   Optional<UserEntity> getUserByEmail(@EmailValid final String email);
}
