package com.jame.dev.gymApp.infrastructure.cache;

public interface BlacklistService {
   void blacklistToken(final String key);
   boolean isBlacklisted(final String key);
}
