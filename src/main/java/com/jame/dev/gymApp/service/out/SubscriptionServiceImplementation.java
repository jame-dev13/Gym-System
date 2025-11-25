package com.jame.dev.gymApp.service.out;

import com.jame.dev.gymApp.entity.SubscriptionEntity;
import com.jame.dev.gymApp.model.dto.in.SubscriptionDtoInput;
import com.jame.dev.gymApp.repository.SubscriptionRepository;
import com.jame.dev.gymApp.service.in.SubscriptionService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImplementation implements SubscriptionService {
   private final SubscriptionRepository repo;

   @Override
   public List<SubscriptionEntity> getAll() {
      return List.of();
   }

   @Override
   public Optional<SubscriptionEntity> getById(@NonNull Long id) {
      return Optional.empty();
   }

   @Override
   public SubscriptionEntity save(@NonNull SubscriptionDtoInput dto) {
      return null;
   }

   @Override
   public SubscriptionEntity update(@NonNull SubscriptionDtoInput dto) {
      return null;
   }

   @Override
   public void softDeleteById(@NonNull Long id) {

   }
}
