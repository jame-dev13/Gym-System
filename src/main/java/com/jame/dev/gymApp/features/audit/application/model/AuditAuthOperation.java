package com.jame.dev.gymApp.features.audit.application.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AuditAuthOperation {
   SIGN_IN("Local SignIn"),
   REGISTER("Register"),
   LOGOUT("Logout"),
   OAUTH_SIGN_IN("Oauth2 SignIn");
   private final String op;
}
