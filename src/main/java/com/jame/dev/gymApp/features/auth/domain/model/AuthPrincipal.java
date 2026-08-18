package com.jame.dev.gymApp.features.auth.domain.model;

public sealed interface AuthPrincipal permits
UserPrincipal, CustomOAuth2User {

   Long id();

   String username();
}
