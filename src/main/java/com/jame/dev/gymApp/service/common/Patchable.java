package com.jame.dev.gymApp.service.common;

import com.jame.dev.gymApp.aspects.annotations.constraints.Minimum;

public interface Patchable<E> {
   E patch (@Minimum long Id);
}
