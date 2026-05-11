package com.jame.dev.gymApp.features.subscription.application.contract;

import com.jame.dev.gymApp.features.subscription.domain.model.PeriodEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.Period;
import lombok.NonNull;

import java.util.Optional;

public interface PeriodService {
   Optional<PeriodEntity> findByPeriod(@NonNull final Period period);
}
