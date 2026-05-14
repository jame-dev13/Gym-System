package com.jame.dev.gymApp.features.auth.domain.model;

import lombok.*;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@AllArgsConstructor
@Getter
@Builder
@ToString
public class UserPrincipal implements UserDetails {

   private final Long id;

   private final String email;

   private final String password;

   private final boolean active;

   private final Collection<? extends GrantedAuthority> authorities;

   @Override
   @NonNull
   public Collection<? extends GrantedAuthority> getAuthorities() {
      return authorities;
   }

   @Override
   public @Nullable String getPassword() {
      return this.password;
   }

   @Override
   @NonNull
   public String getUsername() {
      return this.email;
   }

}
