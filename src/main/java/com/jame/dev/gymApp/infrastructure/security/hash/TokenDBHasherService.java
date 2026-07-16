package com.jame.dev.gymApp.infrastructure.security.hash;

public interface TokenDBHasherService {
   String hashToken(final String rawToken);
   boolean tokenMatches(final String rawToken, final String hashedToken);
}
