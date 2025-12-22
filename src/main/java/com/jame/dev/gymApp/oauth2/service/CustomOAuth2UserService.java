package com.jame.dev.gymApp.oauth2.service;

import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.mapper.RoleMapper;
import com.jame.dev.gymApp.model.dto.in.CustomerDtoInput;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
import com.jame.dev.gymApp.oauth2.model.AuthenticatedUser;
import com.jame.dev.gymApp.oauth2.model.CustomOAuth2User;
import com.jame.dev.gymApp.service.in.CustomerService;
import com.jame.dev.gymApp.service.in.UserService;
import com.jame.dev.gymApp.shared.enums.AuthProvider;
import com.jame.dev.gymApp.shared.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

   private final UserService userService;
   private final CustomerService customerService;
   private final RoleMapper roleMapper;

   @Override
   public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
      final OAuth2User oAuth2User = super.loadUser(userRequest);
      final AuthenticatedUser authenticatedUser = registerUser(oAuth2User);
      final Collection<GrantedAuthority> authorities = roleMapper.roleToGrantedAuthorities(authenticatedUser.roles());
      return new CustomOAuth2User(authenticatedUser, oAuth2User.getAttributes(), authorities);
   }

   private AuthenticatedUser registerUser(final OAuth2User oAuth2User){
      final String email = oAuth2User.getAttribute("email");
      final UserEntity user = userService.getUserByEmail(email)
              .orElseGet(() -> registerUser(oAuth2User, email));
      return AuthenticatedUser.builder()
              .id(user.getId())
              .name(user.getName())
              .email(user.getName())
              .roles(Set.of(Role.USER))
              .build();
   }

   private @NonNull UserEntity registerUser(final OAuth2User oAuth2User, final String email) {
      final UserDtoInput userDtoInput = UserDtoInput.builder()
              .name(oAuth2User.getName())
              .email(email)
              .password(UUID.randomUUID().toString())
              .authProvider(AuthProvider.GOOGLE)
              .roles(Set.of(Role.USER))
              .build();
      final UserEntity userSaved = userService.save(userDtoInput);
      final CustomerDtoInput customerDtoInput = CustomerDtoInput.builder()
              .email(userSaved.getEmail()).build();
      customerService.save(customerDtoInput);
      return userSaved;
   }
}
