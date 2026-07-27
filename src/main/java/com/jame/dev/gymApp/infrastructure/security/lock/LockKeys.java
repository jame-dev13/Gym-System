package com.jame.dev.gymApp.infrastructure.security.lock;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LockKeys {

   PG_RESTORE(":pg_restore"),
   PG_DUMP(":pg_dump");

   private final String key;
}
