package com.jame.dev.gymApp.oauth2.model;

import lombok.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {
   private final AuthenticatedUser user;
   private final Map<String, Object> attributes;
   private final Collection<? extends GrantedAuthority> authorities;

   public CustomOAuth2User(AuthenticatedUser user, Map<String, Object> attributes, Collection<? extends GrantedAuthority> authorities) {
      this.user = user;
      this.attributes = attributes;
      this.authorities = authorities;
   }

   public AuthenticatedUser getUser(){
      return user;
   }

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
      return user.name();
   }
}
