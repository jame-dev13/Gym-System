package com.jame.dev.gymApp.service.out;

import com.jame.dev.gymApp.entity.MemberShipEntity;
import com.jame.dev.gymApp.repository.MembershipRepository;
import com.jame.dev.gymApp.service.in.MembershipService;
import com.jame.dev.gymApp.shared.enums.Membership;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MembershipServiceImplementation implements MembershipService {
   private final MembershipRepository repo;

   @Override
   public Optional<MemberShipEntity> getByMembership(@NonNull Membership membership) {
      return Optional.empty();
   }

   @Override
   public List<MemberShipEntity> getAll() {
      return List.of();
   }

   @Override
   public MemberShipEntity save(@NonNull MemberShipEntity entity) {
      return null;
   }

   @Override
   public Optional<MemberShipEntity> findById(@NonNull Integer id) {
      return Optional.empty();
   }

   @Override
   public void deleteById(@NonNull Integer id) {

   }
}
