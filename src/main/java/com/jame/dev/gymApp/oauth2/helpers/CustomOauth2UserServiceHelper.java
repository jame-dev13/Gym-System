package com.jame.dev.gymApp.oauth2.helpers;

import com.jame.dev.gymApp.mapper.RoleMapper;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
import com.jame.dev.gymApp.model.dto.out.UserDtoOutput;
import com.jame.dev.gymApp.oauth2.model.AuthenticatedUser;
import com.jame.dev.gymApp.service.in.UserService;
import com.jame.dev.gymApp.shared.enums.AuthProvider;
import com.jame.dev.gymApp.shared.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomOauth2UserServiceHelper {
   private final UserService userService;
   private final RoleMapper roleMapper;

   public AuthenticatedUser createOrSaveUser(final OAuth2User oAuth2User) {
      final String email = oAuth2User.getAttribute("email");
      final UserDtoOutput user = userService.getUserByEmail(email)
              .map(u -> new UserDtoOutput(
                      u.getId(),
                      u.getName(),
                      u.getEmail(),
                      roleMapper.toRoleSet(u.getRoles())
              ))
              .orElseGet(() -> saveUser(oAuth2User));
      return AuthenticatedUser.builder()
              .id(user.id())
              .name(user.name())
              .email(user.email())
              .roles(Set.of(Role.USER))
              .build();
   }

   public Collection<GrantedAuthority> getAuthoritiesFrom(final AuthenticatedUser authenticatedUser) {
      final Set<Role> roles = authenticatedUser.roles();
      return roleMapper.rolesToGrantedAuthorities(roles);
   }

   public UserDtoOutput saveUser(final OAuth2User oAuth2User) {
      final UserDtoInput userDtoInput = UserDtoInput.builder()
              .name(oAuth2User.getAttribute("name"))
              .email(oAuth2User.getAttribute("email"))
              .password(UUID.randomUUID().toString())
              .authProvider(AuthProvider.GOOGLE)
              .roles(Set.of(Role.USER))
              .build();
      return userService.save(userDtoInput);
   }

}
