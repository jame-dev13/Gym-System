package com.jame.dev.gymApp.features.auth.application.contract;

public interface AuthenticationChecksService {
   boolean isLocalProvider(final String userEmail);
   boolean userExists(final String userEmail);
   boolean checkExistence(final String userEmail);
}
