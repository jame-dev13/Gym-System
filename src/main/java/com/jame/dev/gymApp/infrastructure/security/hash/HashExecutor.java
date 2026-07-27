package com.jame.dev.gymApp.infrastructure.security.hash;

public interface HashExecutor {
   String hash(final String rawToken);
   boolean verify(final String rawToken, final String hashedToken);
}
