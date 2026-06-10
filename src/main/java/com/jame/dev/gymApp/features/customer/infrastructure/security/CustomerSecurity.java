package com.jame.dev.gymApp.features.customer.infrastructure.security;

import com.jame.dev.gymApp.application.contract.IdentityExtractorService;
import com.jame.dev.gymApp.features.customer.api.request.CustomerRequest;
import com.jame.dev.gymApp.features.customer.application.contract.CustomerOwnershipService;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerValidationRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component("customerSecurity")
@RequiredArgsConstructor
public class CustomerSecurity implements CustomerOwnershipService {

   private final CustomerValidationRepository customerValidationRepository;
   private final IdentityExtractorService extractorService;

   @Override
   public boolean isOwner(final long id, @NonNull final Authentication authentication) {
      final String authName = extractorService.extract(authentication);
      if (authName == null || authName.isBlank()) {
         return false;
      }
      return customerValidationRepository.existsByIdAndUserEmail(id, authName);
   }

   @Override
   public boolean isOwner(final String email, @NonNull final Authentication authentication) {
      final String authName = extractorService.extract(authentication);
      if(authName == null || authName.isBlank())
         return false;
      return email.equals(authName);
   }

   @Override
   public boolean isOwner(CustomerRequest input, @NonNull Authentication authentication) {
      final String authName = extractorService.extract(authentication);
      if(authName == null || authName.isBlank())
         return false;
      return input.email().equals(authName);
   }
}
