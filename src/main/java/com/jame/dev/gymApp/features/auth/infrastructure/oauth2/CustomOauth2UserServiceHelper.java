package com.jame.dev.gymApp.features.auth.infrastructure.oauth2;

import com.jame.dev.gymApp.features.auth.application.model.AuthProvider;
import com.jame.dev.gymApp.features.auth.domain.model.AuthenticatedUser;
import com.jame.dev.gymApp.features.user.api.request.UserRequest;
import com.jame.dev.gymApp.features.user.api.response.UserResponse;
import com.jame.dev.gymApp.features.user.application.contract.UserService;
import com.jame.dev.gymApp.features.user.application.support.mapper.RoleMapper;
import com.jame.dev.gymApp.features.user.domain.model.Role;
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
      final UserResponse user = userService.getUserByEmail(email)
         .map(u -> new UserResponse(
            u.getId(),
            u.getName(),
            u.getEmail(),
            u.getProvider(),
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

   public UserResponse saveUser(final OAuth2User oAuth2User) {
      final UserRequest userRequest = UserRequest.builder()
         .name(oAuth2User.getAttribute("name"))
         .email(oAuth2User.getAttribute("email"))
         .password(UUID.randomUUID().toString())
         .authProvider(AuthProvider.GOOGLE)
         .roles(Set.of(Role.USER))
         .build();
      return userService.save(userRequest);
   }

}
