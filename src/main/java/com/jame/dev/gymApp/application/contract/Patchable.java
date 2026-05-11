package com.jame.dev.gymApp.application.contract;

import com.jame.dev.gymApp.infrastructure.annotation.Minimum;

public interface Patchable<E> {
   E patch (@Minimum long Id);
}
