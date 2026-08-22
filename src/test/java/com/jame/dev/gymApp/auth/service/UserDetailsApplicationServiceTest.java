package com.jame.dev.gymApp.auth.service;

import com.jame.dev.gymApp.domain.exception.NoActiveException;
import com.jame.dev.gymApp.features.auth.application.service.UserDetailsApplicationService;
import com.jame.dev.gymApp.features.auth.domain.repository.AuthenticationChecksQueriesRepository;
import com.jame.dev.gymApp.features.user.application.support.mapper.RoleMapper;
import com.jame.dev.gymApp.features.user.domain.exception.UserEntityNotFoundException;
import com.jame.dev.gymApp.features.user.domain.exception.UserNotVerifiedException;
import com.jame.dev.gymApp.features.user.domain.model.RoleEntity;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import com.jame.dev.gymApp.features.user.domain.repository.UserQueryRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class UserDetailsApplicationServiceTest {

   @Mock
   UserQueryRepository userQueryRepository;
   @Mock
   RoleMapper roleMapper;
   @Mock
   AuthenticationChecksQueriesRepository queriesRepository;

   @InjectMocks
   UserDetailsApplicationService service;

   @Test
   @DisplayName("Should successfully load UserDetails when user is active")
   void loadUserByUsernameShouldSucceedWhenUserIsActive() {
      String email = "active@test.com";
      UserEntity userEntity = mock(UserEntity.class);
      Collection<GrantedAuthority> authorities = Set.of(mock(GrantedAuthority.class));

      given(queriesRepository.existsButNotVerified(anyString())).willReturn(false);
      given(queriesRepository.existsDeactivatedByEmail(anyString())).willReturn(false);
      given(userQueryRepository.findByEmail(email)).willReturn(Optional.of(userEntity));
      given(userEntity.getRoles()).willReturn(Set.of(new RoleEntity()));
      given(roleMapper.entityToGrantedAuthorities(any())).willReturn(authorities);
      given(userEntity.getEmail()).willReturn(email);
      given(userEntity.getPassword()).willReturn("encoded-password");

      UserDetails result = assertDoesNotThrow(() -> service.loadUserByUsername(email));

      assertNotNull(result);
      assertEquals(email, result.getUsername());
      assertTrue(result.isEnabled());
      verify(queriesRepository).existsButNotVerified(anyString());
      verify(queriesRepository).existsDeactivatedByEmail(anyString());
      verify(userQueryRepository).findByEmail(email);
      verify(roleMapper).entityToGrantedAuthorities(any());
      verifyNoMoreInteractions(queriesRepository, userQueryRepository, roleMapper);
   }

   @Test
   @DisplayName("Should throw UserNotVerifiedException")
   void loadUser_DoesThrow_UserNotVerifiedException() {
      String email = "user@mail.com";
      given(queriesRepository.existsButNotVerified(anyString())).willReturn(true);

      assertThrowsExactly(UserNotVerifiedException.class, () -> service.loadUserByUsername(email));

      verify(queriesRepository, atLeastOnce()).existsButNotVerified(anyString());
      verifyNoMoreInteractions(queriesRepository);
      verifyNoInteractions(userQueryRepository, roleMapper);
   }

   @Test
   @DisplayName("Should throw NoActiveException")
   void loadUser_DoesThrow_NoActiveException() {
      String email = "user@mail.com";
      given(queriesRepository.existsButNotVerified(anyString())).willReturn(false);
      given(queriesRepository.existsDeactivatedByEmail(anyString())).willReturn(true);

      assertThrowsExactly(NoActiveException.class, () -> service.loadUserByUsername(email));

      verify(queriesRepository, atLeastOnce()).existsButNotVerified(anyString());
      verify(queriesRepository, atLeastOnce()).existsButNotVerified(anyString());
      verifyNoMoreInteractions(queriesRepository);
      verifyNoInteractions(userQueryRepository, roleMapper);
   }


   @Test
   @DisplayName("Should throw UserEntityNotFoundException when user does not exist")
   void loadUserByUsernameShouldThrowExceptionWhenUserNotFound() {
      String email = "nonexistent@test.com";

      given(queriesRepository.existsButNotVerified(anyString())).willReturn(false);
      given(queriesRepository.existsDeactivatedByEmail(anyString())).willReturn(false);
      given(userQueryRepository.findByEmail(email)).willReturn(Optional.empty());

      assertThrowsExactly(UserEntityNotFoundException.class, () -> service.loadUserByUsername(email));
      verify(queriesRepository, atLeastOnce()).existsButNotVerified(anyString());
      verify(queriesRepository, atLeastOnce()).existsButNotVerified(anyString());
      verify(userQueryRepository, atLeastOnce()).findByEmail(anyString());
      verifyNoMoreInteractions(queriesRepository, userQueryRepository);
      verifyNoInteractions(roleMapper);
      verifyNoInteractions(roleMapper);
   }
}