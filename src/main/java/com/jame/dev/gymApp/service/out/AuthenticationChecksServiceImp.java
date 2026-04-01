package com.jame.dev.gymApp.service.out;

import com.jame.dev.gymApp.repository.AuthenticationChecksQueriesRepository;
import com.jame.dev.gymApp.service.in.AuthenticationChecksService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationChecksServiceImp implements AuthenticationChecksService {

   private final AuthenticationChecksQueriesRepository queriesRepository;

   @Override
   public boolean isLocalProvider(String userEmail) {
      return queriesRepository.isLocalProvider(userEmail);
   }

   @Override
   public boolean userExists(String userEmail) {
      return queriesRepository.existsDeactivatedByEmail(userEmail);
   }

   @Override
   public boolean checkExistence(String userEmail) {
      return queriesRepository.existsButNotVerified(userEmail);
   }
}
