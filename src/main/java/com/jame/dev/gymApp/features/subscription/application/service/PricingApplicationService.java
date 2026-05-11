package com.jame.dev.gymApp.features.subscription.application.service;

import com.jame.dev.gymApp.features.subscription.domain.model.PricingEntity;
import com.jame.dev.gymApp.features.subscription.domain.repository.PricingRepository;
import com.jame.dev.gymApp.features.subscription.application.contract.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
public class PricingApplicationService implements PricingService {
   private final PricingRepository repo;

   @Override
   public List<PricingEntity> getAll() {
      return repo.findAll();
   }

   @Transactional
   @Override
   public PricingEntity save(PricingEntity entity) {
      return repo.save(entity);
   }

   @Override
   public Optional<PricingEntity> getById(int id) {
      return repo.findById(id);
   }
}