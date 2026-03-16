package com.jame.dev.gymApp.service.in;

public interface AuthenticationChecksService {
   boolean isLocalProvider(final String userEmail);
   boolean userExists(final String userEmail);
}
