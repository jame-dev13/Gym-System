package com.jame.dev.gymApp.features.subscription.application.service;

import com.jame.dev.gymApp.features.subscription.domain.model.PeriodEntity;
import com.jame.dev.gymApp.features.subscription.infrastructure.persistence.PeriodRepository;
import com.jame.dev.gymApp.features.subscription.application.contract.PeriodService;
import com.jame.dev.gymApp.features.subscription.domain.model.Period;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PeriodApplicationService implements PeriodService {
   private final PeriodRepository repo;

   @Override
   public Optional<PeriodEntity> findByPeriod(@NonNull Period period) {
      return repo.findByPeriod(period);
   }
}
