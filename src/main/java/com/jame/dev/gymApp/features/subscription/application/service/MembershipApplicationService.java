package com.jame.dev.gymApp.features.subscription.application.service;

import com.jame.dev.gymApp.features.subscription.domain.model.MembershipEntity;
import com.jame.dev.gymApp.features.subscription.infrastructure.persistence.MembershipRepository;
import com.jame.dev.gymApp.features.subscription.application.contract.MembershipService;
import com.jame.dev.gymApp.features.subscription.domain.model.Membership;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@CheckLockProcess
public class MembershipApplicationService implements MembershipService {
   private final MembershipRepository repo;

   @Override
   public List<MembershipEntity> getAll() {
      return repo.findAll();
   }

   @Override
   public Optional<MembershipEntity> getByMembership(@NonNull Membership membership) {
      return repo.findByMembership(membership);
   }

   @Override
   @Transactional
   public MembershipEntity save(@NonNull MembershipEntity entity) {
      return repo.save(entity);
   }
}
