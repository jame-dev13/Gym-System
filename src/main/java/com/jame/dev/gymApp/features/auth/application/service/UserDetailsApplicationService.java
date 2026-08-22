package com.jame.dev.gymApp.features.auth.application.service;

import com.jame.dev.gymApp.domain.exception.NoActiveException;
import com.jame.dev.gymApp.features.auth.domain.model.UserPrincipal;
import com.jame.dev.gymApp.features.auth.domain.repository.AuthenticationChecksQueriesRepository;
import com.jame.dev.gymApp.features.user.application.support.mapper.RoleMapper;
import com.jame.dev.gymApp.features.user.domain.exception.UserEntityNotFoundException;
import com.jame.dev.gymApp.features.user.domain.exception.UserNotVerifiedException;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import com.jame.dev.gymApp.features.user.domain.repository.UserQueryRepository;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import com.jame.dev.gymApp.infrastructure.security.lock.LockKeys;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@CheckLockProcess(keys = {LockKeys.PG_RESTORE})
public class UserDetailsApplicationService implements UserDetailsService {
   private final UserQueryRepository userQueryRepository;
   private final RoleMapper roleMapper;
   private final AuthenticationChecksQueriesRepository queriesRepository;

   @Override
   @NonNull
   public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
      if (queriesRepository.existsButNotVerified(username))
         throw new UserNotVerifiedException(username + " exists but isn't verified.");

      if (queriesRepository.existsDeactivatedByEmail(username))
         throw new NoActiveException(username + " is linked to a deactivated account.");

      final UserEntity userEntity = userQueryRepository.findByEmail(username)
         .orElseThrow(() -> new UserEntityNotFoundException("User Not Found for: " + username));

      return UserPrincipal.builder()
         .id(userEntity.getId())
         .username(userEntity.getEmail())
         .password(userEntity.getPassword())
         .authorities(roleMapper.entityToGrantedAuthorities(userEntity.getRoles()))
         .build();
   }
}
