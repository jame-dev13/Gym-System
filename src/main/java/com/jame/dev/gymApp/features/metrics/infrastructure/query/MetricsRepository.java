package com.jame.dev.gymApp.features.metrics.infrastructure.query;

import lombok.NonNull;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

@NoRepositoryBean
public interface MetricsRepository<T, ID> extends Repository<@NonNull T, @NonNull ID> {
}
