package com.jame.dev.gymApp.features.subscription.application.service;

import com.jame.dev.gymApp.features.subscription.domain.model.MemberShipEntity;
import com.jame.dev.gymApp.features.subscription.infrastructure.persistence.MembershipRepository;
import com.jame.dev.gymApp.features.subscription.application.contract.MembershipService;
import com.jame.dev.gymApp.features.subscription.domain.model.Membership;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MembershipApplicationService implements MembershipService {
   private final MembershipRepository repo;

   @Override
   public List<MemberShipEntity> getAll() {
      return repo.findAll();
   }

   @Override
   public Optional<MemberShipEntity> getByMembership(@NonNull Membership membership) {
      return repo.findByMembership(membership);
   }

   @Override
   @Transactional
   public MemberShipEntity save(@NonNull MemberShipEntity entity) {
      return repo.save(entity);
   }
}
