package com.jame.dev.gymApp.service.out;

import com.jame.dev.gymApp.entity.VerificationEntity;
import com.jame.dev.gymApp.exception.VerificationAttemptFailedException;
import com.jame.dev.gymApp.exception.VerificationNotFoundException;
import com.jame.dev.gymApp.mapper.RoleMapper;
import com.jame.dev.gymApp.repository.CustomerRepository;
import com.jame.dev.gymApp.repository.RoleRepository;
import com.jame.dev.gymApp.repository.UserRepository;
import com.jame.dev.gymApp.repository.VerificationRepository;
import com.jame.dev.gymApp.service.in.AccountRecoveryService;
import com.jame.dev.gymApp.shared.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountRecoveryServiceImplementation implements AccountRecoveryService {
   private final VerificationRepository verificationRepository;
   private final PasswordEncoder passwordEncoder;
   private final UserRepository userRepository;
   private final CustomerRepository customerRepository;
   private final RoleMapper roleMapper;
   private final RoleRepository roleRepository;

   private @NonNull VerificationEntity getVerificationEntity(String userEmail, String token) {
      final VerificationEntity VE = verificationRepository.findDeactivatedByUser_Email(userEmail)
              .orElseThrow(() -> new VerificationNotFoundException("Verified user account's not found."));
      boolean tokenMatch = passwordEncoder.matches(token, VE.getToken());
      if (!tokenMatch) {
         throw new VerificationAttemptFailedException("Token mismatch.");
      }
      return VE;
   }

   @Override
   @Transactional
   public void reActivateUserAccount(String userEmail, String token) {
      final VerificationEntity VE = getVerificationEntity(userEmail, token);
      userRepository.findByEmail(userEmail)
              .ifPresent(u -> {
                 u.setActive(true);
                 u.setUpdatedAt(Instant.now());
                 u.setRoles(roleMapper.toEntitySet(Set.of(Role.USER), roleRepository));
                 VE.setUser(u);
              });
   }

   @Override
   @Transactional
   public void reactivateCustomerAccount(String userEmail, String token) {
      final VerificationEntity VE = getVerificationEntity(userEmail, token);

      customerRepository.findDeactivatedByUser_email(userEmail)
              .ifPresent(CE -> {
                 CE.setActive(true);
                 CE.setUpdatedAt(Instant.now());
                 VE.setUser(CE.getUser());
              });
   }

   @Override
   public boolean accountExists(String userEmail) {
      return verificationRepository.existsDeactivatedByUser_Email(userEmail);
   }
}
