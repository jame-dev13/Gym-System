package com.jame.dev.gymApp.mapper;

import com.jame.dev.gymApp.entity.RoleEntity;
import com.jame.dev.gymApp.repository.RoleRepository;
import com.jame.dev.gymApp.shared.enums.Role;
import lombok.NonNull;
import org.mapstruct.Mapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RoleMapper {

   default Set<Role> authoritiesToRoles(@NonNull final Collection<? extends GrantedAuthority> authorities) {
      return authorities.stream()
              .map(GrantedAuthority::getAuthority)
              .filter(auth -> auth != null && auth.startsWith("ROLE_"))
              .map(auth -> auth.replaceFirst("ROLE_", ""))
              .map(Role::valueOf)
              .collect(Collectors.toSet());
   }

   default Collection<GrantedAuthority> roleToGrantedAuthorities(Set<Role> roles) {
      return roles.stream()
              .map(r -> new SimpleGrantedAuthority("ROLE_" + r.name()))
              .collect(Collectors.toSet());
   }

   default Collection<GrantedAuthority> entityToGrantedAuthorities(Set<RoleEntity> roles) {
      return roles.stream()
              .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getRole().name()))
              .collect(Collectors.toSet());
   }

   default Set<RoleEntity> toEntitySet(final Set<Role> roles, RoleRepository roleRepository) {
      return roles.stream()
              .map(r -> toEntity(r, roleRepository))
              .filter(Objects::nonNull)
              .collect(Collectors.toSet());
   }

   default Set<Role> toRoleSet(Set<RoleEntity> roles) {
      return roles.stream()
              .map(RoleEntity::getRole)
              .collect(Collectors.toSet());
   }

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
