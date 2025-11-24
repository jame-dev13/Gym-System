package com.jame.dev.gymApp.repository;

import com.jame.dev.gymApp.entity.PeriodEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PeriodRepository extends JpaRepository<@NonNull PeriodEntity, @NonNull Long> {
}
