package com.jame.dev.gymApp.features.auth.application.service;

import com.jame.dev.gymApp.features.auth.domain.exception.AuthenticationNullException;
import com.jame.dev.gymApp.features.auth.domain.exception.ExtractClaimException;
import com.jame.dev.gymApp.features.auth.domain.exception.IllegalSubjectAuthenticatedException;
import com.jame.dev.gymApp.features.auth.application.support.factory.AuthResponsesFactory;
import com.jame.dev.gymApp.features.auth.application.contract.JwtService;
import com.jame.dev.gymApp.features.auth.api.response.SessionResponse;
import com.jame.dev.gymApp.features.auth.infrastructure.security.identity.IdentityExtractorService;
import com.jame.dev.gymApp.features.auth.application.contract.session.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Validated
public class SessionApplicationService implements SessionService {
   private final IdentityExtractorService identityExtractorService;
   private final JwtService jwtService;
   private final AuthResponsesFactory authResponsesFactory;

   @Override
   public SessionResponse getSession(
           final String access,
           final Authentication authentication) {
      if (Objects.isNull(access) || Objects.isNull(authentication)) {
         throw new AuthenticationNullException("Not valid access authentication.");
      }

      final String tokenSubject = jwtService.extractSubject(access)
              .orElseThrow(() -> new ExtractClaimException("Claim not found."));

      final String subjectExpected = identityExtractorService.extract(authentication);
      if (!tokenSubject.equals(subjectExpected))
         throw new IllegalSubjectAuthenticatedException("Subjects doesn't match.");

      return authResponsesFactory.createSessionFrom(tokenSubject, authentication.getAuthorities());
   }
}
