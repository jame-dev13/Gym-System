package com.jame.dev.gymApp.features.auth.domain.model;

import lombok.Builder;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Builder
public record UserPrincipal(
   Long id,
   String username,
   Collection<? extends GrantedAuthority> authorities
) implements UserDetails, AuthPrincipal {

   @Override
   public Long id() {
      return id;
   }

   @Override
   public String username() {
      return username;
   }

   @Override
   @NullMarked
   public Collection<? extends GrantedAuthority> getAuthorities() {
      return authorities;
   }

   @Override
   public @Nullable String getPassword() {
      return null;
   }

   @Override
   @NullMarked
   public String getUsername() {
      return username;
   }
}
