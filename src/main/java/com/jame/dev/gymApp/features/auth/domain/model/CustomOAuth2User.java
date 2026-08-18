package com.jame.dev.gymApp.features.auth.domain.model;

import com.jame.dev.gymApp.features.auth.application.model.AuthProvider;
import lombok.Builder;
import lombok.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Builder
public record CustomOAuth2User(
   Long id,
   String username,
   AuthProvider provider,
   Map<String, Object> attributes,
   Collection<? extends GrantedAuthority> authorities
) implements OAuth2User, AuthPrincipal {

   @Override
   public Map<String, Object> getAttributes() {
      return attributes;
   }

   @Override
   public Collection<? extends GrantedAuthority> getAuthorities() {
      return authorities;
   }

   @Override
   public @NonNull String getName() {
      return this.username;
   }

   @Override
   public Long id() {
      return this.id;
   }

   @Override
   public String username() {
      return this.username;
   }
}
