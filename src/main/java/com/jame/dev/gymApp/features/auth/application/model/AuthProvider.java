package com.jame.dev.gymApp.features.auth.application.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthProvider {
   GOOGLE("google"),
   FACEBOOK("facebook"),
   LOCAL("local");

   private final String provider;
}
