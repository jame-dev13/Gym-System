package com.jame.dev.gymApp.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomMongoSpecification<T> {
   Page<T> search(final Pageable pageable, final String search);
}
