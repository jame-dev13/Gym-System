package com.jame.dev.gymApp.service.in;

public interface TokenDBHasherService {
   String hashToken(final String rawToken);
   boolean tokenMatches(final String rawToken, final String hashedToken);
}
