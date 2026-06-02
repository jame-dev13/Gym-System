package com.jame.dev.gymApp.features.audit.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuditLogRepositoryCustom<T> {
   Page<T> search(Pageable pageable, String search);
}
