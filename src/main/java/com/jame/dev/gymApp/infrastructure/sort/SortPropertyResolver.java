package com.jame.dev.gymApp.infrastructure.sort;

import org.springframework.data.domain.Pageable;

public interface SortPropertyResolver {
   Pageable resolve(Pageable pageable);
}
