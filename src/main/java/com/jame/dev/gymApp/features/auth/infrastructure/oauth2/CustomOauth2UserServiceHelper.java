package com.jame.dev.gymApp.features.auth.infrastructure.oauth2;

import com.jame.dev.gymApp.features.auth.application.model.AuthProvider;
import com.jame.dev.gymApp.features.auth.domain.model.AuthenticatedUser;
import com.jame.dev.gymApp.features.user.api.request.UserRequest;
import com.jame.dev.gymApp.features.user.api.response.UserResponse;
import com.jame.dev.gymApp.features.user.application.contract.UserFactory;
import com.jame.dev.gymApp.features.user.application.support.mapper.RoleMapper;
import com.jame.dev.gymApp.features.user.domain.exception.CantSaveUserException;
import com.jame.dev.gymApp.features.user.domain.model.Role;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import com.jame.dev.gymApp.features.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomOauth2UserServiceHelper {
   private final UserRepository userRepository;
   private final UserFactory userFactory;
   private final RoleMapper roleMapper;

   @Transactional
   public AuthenticatedUser createOrSaveUser(final OAuth2User oAuth2User) {
      final String email = oAuth2User.getAttribute("email");
      if (email == null) {
         throw new CantSaveUserException("Some fields are not valid to proceed.");
      }
      final UserResponse user = userRepository.findByEmail(email)
         .map(userFactory::createFromEntity)
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
      final UserEntity user = userRepository.save(userFactory.createFromInput(userRequest));
      return userFactory.createFromEntity(user);
   }

}
