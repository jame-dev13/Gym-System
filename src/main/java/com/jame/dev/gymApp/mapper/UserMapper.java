package com.jame.dev.gymApp.mapper;

import com.jame.dev.gymApp.entity.RoleEntity;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
import com.jame.dev.gymApp.model.dto.out.UserDtoOutput;
import org.mapstruct.Builder;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        uses = {RoleMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        builder = @Builder)
public interface UserMapper {
   UserDtoOutput toDto(UserEntity user);

   default UserEntity toEntity(UserDtoInput dto) {
      if (dto == null) return null;
      return UserEntity.builder()
              .name(dto.name())
              .email(dto.email())
              .roles(dto.roles().stream()
                      .map(r -> new RoleEntity(null, r))
                      .collect(Collectors.toSet()))
              .active(true)
              .build();
   }
}
