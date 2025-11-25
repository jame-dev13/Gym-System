package com.jame.dev.gymApp.service.out;

import com.jame.dev.gymApp.entity.MemberShipEntity;
import com.jame.dev.gymApp.exception.NoOperationException;
import com.jame.dev.gymApp.repository.MembershipRepository;
import com.jame.dev.gymApp.service.in.MembershipService;
import com.jame.dev.gymApp.shared.enums.Membership;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MembershipServiceImplementation implements MembershipService {
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

   @Override
   public Optional<MemberShipEntity> findById(@NonNull Integer id) {
      return repo.findById(id);
   }

   @Override
   @Transactional
   public void deleteById(@NonNull Integer id) {
      throw new NoOperationException("Unsupported Operation");
   }
}
