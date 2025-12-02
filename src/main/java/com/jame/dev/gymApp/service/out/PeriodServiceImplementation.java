package com.jame.dev.gymApp.service.out;

import com.jame.dev.gymApp.entity.PeriodEntity;
import com.jame.dev.gymApp.repository.PeriodRepository;
import com.jame.dev.gymApp.service.in.PeriodService;
import com.jame.dev.gymApp.shared.enums.Period;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PeriodServiceImplementation implements PeriodService {
   private final PeriodRepository repo;

   @Override
   public Optional<PeriodEntity> findByPeriod(@NonNull Period period) {
      return repo.findByPeriod(period);
   }
}
