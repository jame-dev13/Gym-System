package com.jame.dev.gymApp.factories.in;

import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
import com.jame.dev.gymApp.model.dto.out.PageDto;
import com.jame.dev.gymApp.model.dto.out.UserDtoOutput;
import com.jame.dev.gymApp.model.dto.out.UserMinimalInfo;
import org.springframework.data.domain.Page;

public non-sealed interface UserFactory extends Factory<
   UserEntity, UserDtoOutput, UserDtoInput> {
   PageDto<UserMinimalInfo> createMinimalInfoPage(final Page<UserMinimalInfo> page);
}
