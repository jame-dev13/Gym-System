package com.jame.dev.gymApp.features.auth.application.service;

import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import com.jame.dev.gymApp.domain.exception.NoActiveException;
import com.jame.dev.gymApp.features.user.domain.exception.UserEntityNotFoundException;
import com.jame.dev.gymApp.features.user.application.support.mapper.RoleMapper;
import com.jame.dev.gymApp.features.user.application.contract.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;

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
      if(!userEntity.isActive()) {
         throw new NoActiveException("This account is deactivated.");
      }
      final Collection<GrantedAuthority> authorities =
              roleMapper.entityToGrantedAuthorities(userEntity.getRoles());
      return User.builder()
              .username(userEntity.getEmail())
              .password(userEntity.getPassword())
              .disabled(false)
              .accountExpired(false)
              .credentialsExpired(false)
              .accountLocked(false)
              .authorities(authorities)
              .build();
   }
}
