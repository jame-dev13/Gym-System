package com.jame.dev.gymApp.auth.service;

import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.exception.UserEntityNotFoundException;
import com.jame.dev.gymApp.mapper.RoleMapper;
import com.jame.dev.gymApp.service.in.UserService;
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
public class UserDetailsServiceImplementation implements UserDetailsService {
   private final UserService service;
   private final RoleMapper roleMapper;

   @Override
   @NonNull
   public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
      final UserEntity userEntity = service.getUserByEmail(username)
              .orElseThrow(() -> new UserEntityNotFoundException("User Not Found."));
      Collection<GrantedAuthority> authorities =
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
