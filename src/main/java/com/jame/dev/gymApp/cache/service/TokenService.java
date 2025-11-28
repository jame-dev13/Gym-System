package com.jame.dev.gymApp.cache.service;

public interface TokenService {
   void blacklistToken(final String key);
   boolean isBlacklisted(final String key);
}
