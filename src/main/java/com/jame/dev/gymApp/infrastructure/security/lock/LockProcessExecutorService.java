package com.jame.dev.gymApp.infrastructure.security.lock;

public interface LockProcessExecutorService {
   void lock(final String processKey);
   boolean isLocked(final String processKey);
   void releaseLock(final String processKey);
}
