package com.jame.dev.gymApp.service.in;

import com.jame.dev.gymApp.entity.PeriodEntity;
import com.jame.dev.gymApp.shared.enums.Period;
import lombok.NonNull;

import java.util.Optional;

public interface PeriodService {
   Optional<PeriodEntity> findByPeriod(@NonNull final Period period);
}
