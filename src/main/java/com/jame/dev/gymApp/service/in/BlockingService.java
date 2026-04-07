package com.jame.dev.gymApp.service.in;

import java.time.Duration;

public interface BlockingService {
   void blockTemporary(String blockingKey, String strikeKey);
   boolean isBlocked(String  blockingKey);
   Duration getBlockingTimeOf(String blockingKey);
}
