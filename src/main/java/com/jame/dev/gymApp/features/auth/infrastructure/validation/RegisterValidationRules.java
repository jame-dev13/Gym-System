package com.jame.dev.gymApp.features.auth.infrastructure.validation;

import com.jame.dev.gymApp.domain.exception.NoActiveException;
import com.jame.dev.gymApp.features.auth.domain.exception.AlreadyExistsException;
import com.jame.dev.gymApp.features.auth.domain.exception.VerificationNotFoundException;
import com.jame.dev.gymApp.features.auth.domain.repository.AuthenticationChecksQueriesRepository;
import com.jame.dev.gymApp.features.user.domain.repository.UserValidationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegisterValidationRules {

   private final AuthenticationChecksQueriesRepository queriesRepository;
   private final UserValidationRepository userValidationRepository;

   public void validateBeforeExecuteService(final String username) {
      if (userValidationRepository.existsAndIsDeactivatedByEmail(username)) {
         throw new NoActiveException("Account already exists but deactivated for: " + username + '.');
      }

      if (queriesRepository.existsButNotVerified(username)) {
         throw new VerificationNotFoundException("User is not verified: " + username + '.');
      }

      if (userValidationRepository.existsByEmail(username)) {
         throw new AlreadyExistsException("Account already exists for: " + username + '.');
      }
   }
}
