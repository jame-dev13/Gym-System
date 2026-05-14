package com.jame.dev.gymApp.features.auth.application.service;

import com.jame.dev.gymApp.domain.exception.NoActiveException;
import com.jame.dev.gymApp.features.auth.domain.model.UserPrincipal;
import com.jame.dev.gymApp.features.user.application.contract.UserService;
import com.jame.dev.gymApp.features.user.application.support.mapper.RoleMapper;
import com.jame.dev.gymApp.features.user.domain.exception.UserEntityNotFoundException;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsApplicationService implements UserDetailsService {
   private final UserService userService;
   private final RoleMapper roleMapper;

   @Override
   @NonNull
   public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
      final UserEntity userEntity = userService.getUserByEmail(username)
         .orElseThrow(() -> new UserEntityNotFoundException("User Not Found."));

      if (!userEntity.isActive()) {
         throw new NoActiveException("This account is deactivated.");
      }

      return UserPrincipal.builder()
         .id(userEntity.getId())
         .email(userEntity.getEmail())
         .password(userEntity.getPassword())
         .active(true)
         .authorities(roleMapper.entityToGrantedAuthorities(userEntity.getRoles()))
         .build();
   }
}
