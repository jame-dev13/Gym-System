package com.jame.dev.gymApp.mapper;

import com.jame.dev.gymApp.entity.RoleEntity;
import com.jame.dev.gymApp.repository.RoleRepository;
import com.jame.dev.gymApp.shared.enums.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {

   default RoleEntity toEntity(Role role, RoleRepository roleRepository){
      if(role == null) return null;
      return roleRepository.findByRole(role)
              .orElse(new RoleEntity(null, role));
   }

   default Role toDto(RoleEntity entity){
      if(entity == null) return null;
      return entity.getRole();
   }
}
