package com.jame.dev.gymApp.service.out;

import com.jame.dev.gymApp.exception.AuthenticationNullException;
import com.jame.dev.gymApp.exception.ExtractClaimException;
import com.jame.dev.gymApp.exception.IllegalSubjectAuthenticatedException;
import com.jame.dev.gymApp.factories.AuthResponsesFactory;
import com.jame.dev.gymApp.jwt.service.JwtService;
import com.jame.dev.gymApp.model.dto.auth.SessionDto;
import com.jame.dev.gymApp.service.in.IdentityExtractorService;
import com.jame.dev.gymApp.service.in.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Validated
public class SessionServiceImplementation implements SessionService {
   private final IdentityExtractorService identityExtractorService;
   private final JwtService jwtService;
   private final AuthResponsesFactory authResponsesFactory;

   @Override
   public SessionDto getSession(
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
