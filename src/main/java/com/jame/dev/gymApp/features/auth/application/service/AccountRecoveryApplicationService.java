package com.jame.dev.gymApp.features.auth.application.service;

import com.jame.dev.gymApp.infrastructure.security.hash.HashExecutor;
import com.jame.dev.gymApp.features.auth.application.contract.recovery.AccountRecoveryService;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import com.jame.dev.gymApp.features.auth.domain.exception.VerificationAttemptFailedException;
import com.jame.dev.gymApp.features.auth.domain.exception.VerificationNotFoundException;
import com.jame.dev.gymApp.features.auth.domain.model.VerificationEntity;
import com.jame.dev.gymApp.features.auth.domain.repository.VerificationRepository;
import com.jame.dev.gymApp.features.customer.infrastructure.persistence.CustomerRepository;
import com.jame.dev.gymApp.features.user.application.support.mapper.RoleMapper;
import com.jame.dev.gymApp.features.user.domain.exception.UserEntityNotFoundException;
import com.jame.dev.gymApp.features.user.domain.model.Role;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import com.jame.dev.gymApp.features.user.infrastructure.persistence.RoleRepository;
import com.jame.dev.gymApp.features.user.infrastructure.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@CheckLockProcess
public class AccountRecoveryApplicationService implements AccountRecoveryService {
   private final VerificationRepository verificationRepository;
   private final HashExecutor hasherService;
   private final UserRepository userRepository;
   private final CustomerRepository customerRepository;
   private final RoleMapper roleMapper;
   private final RoleRepository roleRepository;

   private @NonNull VerificationEntity getVerificationEntity(String userEmail, String token) {
      final VerificationEntity VE = verificationRepository.findDeactivatedByUser_Email(userEmail)
              .orElseThrow(() -> new VerificationNotFoundException("Verified user account's not found."));
      final boolean tokenMatch = hasherService.verify(token, VE.getToken());
      if (!tokenMatch) {
         throw new VerificationAttemptFailedException("Token mismatch.");
      }
      return VE;
   }

   @Override
   @Transactional
   public void reActivateUserAccount(String userEmail, String token) {
      final VerificationEntity VE = getVerificationEntity(userEmail, token);
      final UserEntity user = userRepository.findByEmail(userEmail)
              .orElseThrow(() -> new UserEntityNotFoundException("User not found."));

      if (!user.isActive()) {
         user.setActive(true);
         user.setUpdatedAt(Instant.now());
         user.setRoles(roleMapper.toEntitySet(Set.of(Role.USER), roleRepository));
      }

      final long userId = user.getId();

      if(customerRepository.existsDeactivatedByUserId(userId)) {
         customerRepository.findDeactivatedByUserId(userId)
                 .ifPresent(c -> {
                    if (!c.isActive()) {
                       c.setActive(true);
                       c.setUpdatedAt(Instant.now());
                       c.setUser(user);
                    }
                 });
      }
      VE.setUser(user);
   }

   @Override
   public boolean accountExists(String userEmail) {
      return verificationRepository.existsDeactivatedByUser_Email(userEmail);
   }
}
