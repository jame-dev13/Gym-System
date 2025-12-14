package com.jame.dev.gymApp.service.out;

import com.jame.dev.gymApp.entity.PricingEntity;
import com.jame.dev.gymApp.repository.PricingRepository;
import com.jame.dev.gymApp.service.in.PricingService;
import com.jame.dev.gymApp.shared.enums.Membership;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PricingServiceImplementation implements PricingService {
   private final PricingRepository repo;

   @Override
   public List<PricingEntity> getAll() {
      return repo.findAll();
   }

   @Transactional
   @Override
   public PricingEntity save(@NonNull PricingEntity entity) {
      return repo.save(entity);
   }

   @Override
   public Optional<PricingEntity> getById(int id) {
      return repo.findById(id);
   }

   @Override
   public Optional<PricingEntity> getByMembership(Membership membership) {
      return repo.findByMemberShipEntity_Membership(membership);
   }

}