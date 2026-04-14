package com.jame.dev.gymApp.auth.service;

import com.jame.dev.gymApp.entity.RoleEntity;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.exception.NoActiveException;
import com.jame.dev.gymApp.exception.UserEntityNotFoundException;
import com.jame.dev.gymApp.mapper.RoleMapper;
import com.jame.dev.gymApp.service.in.UserService;
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
class UserDetailsServiceImplementationTest {

   @Mock
   UserService userService;
   @Mock
   RoleMapper roleMapper;

   @InjectMocks
   UserDetailsServiceImplementation service;

   @Test
   @DisplayName("Should successfully load UserDetails when user is active")
   void loadUserByUsernameShouldSucceedWhenUserIsActive() {
      String email = "active@test.com";
      UserEntity userEntity = mock(UserEntity.class);
      Collection<GrantedAuthority> authorities = Set.of(mock(GrantedAuthority.class));

      given(userService.getUserByEmail(email)).willReturn(Optional.of(userEntity));
      given(userEntity.isActive()).willReturn(true);
      given(userEntity.getRoles()).willReturn(Set.of(new RoleEntity()));
      given(roleMapper.entityToGrantedAuthorities(any())).willReturn(authorities);
      given(userEntity.getEmail()).willReturn(email);
      given(userEntity.getPassword()).willReturn("encoded-password");

      UserDetails result = assertDoesNotThrow(() -> service.loadUserByUsername(email));

      assertNotNull(result);
      assertEquals(email, result.getUsername());
      assertTrue(result.isEnabled());
      verify(userService).getUserByEmail(email);
      verify(roleMapper).entityToGrantedAuthorities(any());
   }

   @Test
   @DisplayName("Should throw UserEntityNotFoundException when user does not exist")
   void loadUserByUsernameShouldThrowExceptionWhenUserNotFound() {
      String email = "nonexistent@test.com";
      given(userService.getUserByEmail(email)).willReturn(Optional.empty());

      assertThrowsExactly(UserEntityNotFoundException.class, () -> service.loadUserByUsername(email));

      verifyNoInteractions(roleMapper);
   }

   @Test
   @DisplayName("Should throw NoActiveException when user is deactivated")
   void loadUserByUsernameShouldThrowExceptionWhenUserIsInactive() {
      String email = "inactive@test.com";
      UserEntity userEntity = mock(UserEntity.class);

      given(userService.getUserByEmail(email)).willReturn(Optional.of(userEntity));
      given(userEntity.isActive()).willReturn(false);

      assertThrowsExactly(NoActiveException.class, () -> service.loadUserByUsername(email));

      verifyNoInteractions(roleMapper);
   }
}