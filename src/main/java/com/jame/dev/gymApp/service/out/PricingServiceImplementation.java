package com.jame.dev.gymApp.service.out;

import com.jame.dev.gymApp.entity.PricingEntity;
import com.jame.dev.gymApp.repository.PricingRepository;
import com.jame.dev.gymApp.service.in.PricingService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PricingServiceImplementation implements PricingService {
   private final PricingRepository repo;

   @Override
   public List<PricingEntity> getAll() {
      return List.of();
   }

   @Override
   public PricingEntity save(@NonNull PricingEntity entity) {
      return null;
   }

   @Override
   public Optional<PricingEntity> findById(@NonNull Integer id) {
      return Optional.empty();
   }

   @Override
   public void deleteById(@NonNull Integer id) {

   }
}