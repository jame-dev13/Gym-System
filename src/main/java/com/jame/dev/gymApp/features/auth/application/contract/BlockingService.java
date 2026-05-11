package com.jame.dev.gymApp.features.auth.application.contract;

import java.time.Duration;

public interface BlockingService {
   void blockTemporary(String blockingKey, String strikeKey);
   boolean isBlocked(String  blockingKey);
   Duration getBlockingTimeOf(String blockingKey);
}
