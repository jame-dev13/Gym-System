package com.jame.dev.gymApp.service;

import com.jame.dev.gymApp.exception.AuthenticationNullException;
import com.jame.dev.gymApp.exception.ExtractClaimException;
import com.jame.dev.gymApp.exception.IllegalSubjectAuthenticatedException;
import com.jame.dev.gymApp.factories.AuthResponsesFactory;
import com.jame.dev.gymApp.jwt.service.JwtService;
import com.jame.dev.gymApp.model.dto.auth.SessionDto;
import com.jame.dev.gymApp.service.in.IdentityExtractorService;
import com.jame.dev.gymApp.service.out.SessionServiceImplementation;
import com.jame.dev.gymApp.shared.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SessionServiceTest {

   @Mock
   private IdentityExtractorService identityExtractorService;
   @Mock
   private JwtService jwtService;
   @Mock
   private AuthResponsesFactory authResponsesFactory;

   @InjectMocks
   private SessionServiceImplementation service;

   @Test
   @DisplayName("Should get session")
   void shouldGetSessionDto() {
      Set<Role> roles = Set.of(Role.USER);
      Authentication authentication = mock(Authentication.class);
      String access = "ACCESS";
      String subject = "subject";

      when(jwtService.extractSubject(anyString())).thenReturn(Optional.of(subject));
      when(identityExtractorService.extract(authentication)).thenReturn(subject);

      when(authResponsesFactory.createSessionFrom(anyString(), any()))
              .thenReturn(new SessionDto(subject, roles, true));

      var result = assertDoesNotThrow(() -> service.getSession(access, authentication));
      assertNotNull(result);

      verify(jwtService, atLeastOnce()).extractSubject(anyString());
      verify(identityExtractorService, atLeastOnce()).extract(authentication);
      verify(authResponsesFactory, atLeastOnce()).createSessionFrom(anyString(), any());
      verifyNoMoreInteractions(jwtService, identityExtractorService, authResponsesFactory);
   }

   @Test
   @DisplayName("Should throws exception cause null arguments.")
   void shouldThrowsAuthenticationNullException() {
      assertThrowsExactly(AuthenticationNullException.class,
              () -> service.getSession(null, null));
      verifyNoInteractions(identityExtractorService, authResponsesFactory,  jwtService);
   }

   @Test
   @DisplayName("Should be able to not find the subject claim.")
   void shouldThrowsClamsNotFoundException() {
      Authentication authentication = mock(Authentication.class);
      String access = "NoAccess";
      when(jwtService.extractSubject(anyString())).thenReturn(Optional.empty());
      assertThrowsExactly(ExtractClaimException.class,
              () -> service.getSession(access, authentication));

      verify(jwtService, atLeastOnce()).extractSubject(anyString());
      verifyNoInteractions(authResponsesFactory, identityExtractorService);
      verifyNoMoreInteractions(jwtService);
   }

   @Test
   @DisplayName("Should throws Exception whether the subjects are not equals.")
   void shouldThrowsIllegalSubjectAuthenticatedException() {
      Authentication authentication = mock(Authentication.class);
      String access = "ACCESS";
      String subjectAccess = "Access Subject";
      String subjectExpected = "Access Expected";

      when(jwtService.extractSubject(anyString())).thenReturn(Optional.of(subjectAccess));
      when(identityExtractorService.extract(authentication)).thenReturn(subjectExpected);

      assertThrowsExactly(IllegalSubjectAuthenticatedException.class, () -> service.getSession(access, authentication));

      verify(jwtService, atLeastOnce()).extractSubject(anyString());
      verify(identityExtractorService, atLeastOnce()).extract(authentication);

      verifyNoMoreInteractions(jwtService, identityExtractorService);
      verifyNoInteractions(authResponsesFactory);
   }
}
