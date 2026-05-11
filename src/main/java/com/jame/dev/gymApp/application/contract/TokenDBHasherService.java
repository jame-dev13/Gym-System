package com.jame.dev.gymApp.application.contract;

public interface TokenDBHasherService {
   String hashToken(final String rawToken);
   boolean tokenMatches(final String rawToken, final String hashedToken);
}
