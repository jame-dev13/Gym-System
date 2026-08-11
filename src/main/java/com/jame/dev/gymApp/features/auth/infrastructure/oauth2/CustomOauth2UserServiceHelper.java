package com.jame.dev.gymApp.features.auth.infrastructure.oauth2;

import com.jame.dev.gymApp.features.auth.application.model.AuthProvider;
import com.jame.dev.gymApp.features.auth.domain.model.AuthenticatedUser;
import com.jame.dev.gymApp.features.user.api.request.UserRequest;
import com.jame.dev.gymApp.features.user.application.contract.UserFactory;
import com.jame.dev.gymApp.features.user.application.support.mapper.RoleMapper;
import com.jame.dev.gymApp.features.user.domain.exception.CantSaveUserException;
import com.jame.dev.gymApp.features.user.domain.model.Role;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import com.jame.dev.gymApp.features.user.domain.repository.UserMutationRepository;
import com.jame.dev.gymApp.features.user.domain.repository.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class CustomOauth2UserServiceHelper {
   private final UserQueryRepository userQueryRepository;
   private final UserMutationRepository mutationRepository;
   private final UserFactory userFactory;
   private final RoleMapper roleMapper;

   private static final Function<String, AuthProvider> getAuthProvider =
      valueOf -> Enum.valueOf(AuthProvider.class, valueOf.toUpperCase());

   private static final BiFunction<OAuth2User, String, UserRequest> createUserRequest =
      (user, provider) -> UserRequest.builder()
         .name(user.getAttribute("name"))
         .email(user.getAttribute("email"))
         .password(UUID.randomUUID().toString())
         .authProvider(getAuthProvider.apply(provider))
         .roles(Set.of(Role.USER))
         .build();

   private static final Function<UserEntity, AuthenticatedUser> createAuthenticatedUser =
      user -> AuthenticatedUser.builder()
         .id(user.getId())
         .name(user.getName())
         .email(user.getEmail())
         .authProvider(user.getProvider())
         .roles(Set.of(Role.USER))
         .build();

   @Transactional
   public AuthenticatedUser handleUser(final OAuth2User oAuth2User, final String provider) {
      final String email = oAuth2User.getAttribute("email");

      if (email == null) {
         throw new CantSaveUserException("Some fields are not valid to proceed.");
      }

      return userQueryRepository.findByEmail(email)
         .map(u -> {
            if (!u.isActive()) {
               u.setActive(true);
            }
            return createAuthenticatedUser.apply(u);
         })
         .orElseGet(() -> {
            final UserRequest userRequest = createUserRequest.apply(oAuth2User, provider);
            final UserEntity user = mutationRepository.save(userFactory.createFromInput(userRequest));
            return createAuthenticatedUser.apply(user);
         });
   }

   public Collection<GrantedAuthority> getAuthoritiesFrom(final AuthenticatedUser authenticatedUser) {
      return roleMapper.rolesToGrantedAuthorities(authenticatedUser.roles());
   }

}
