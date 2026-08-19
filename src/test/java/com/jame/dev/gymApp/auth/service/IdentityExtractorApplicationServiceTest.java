package com.jame.dev.gymApp.auth.service;

import com.jame.dev.gymApp.features.auth.application.model.AuthProvider;
import com.jame.dev.gymApp.features.auth.application.service.IdentityExtractorApplicationService;
import com.jame.dev.gymApp.features.auth.domain.exception.InvalidAuthenticationPrincipalException;
import com.jame.dev.gymApp.features.auth.domain.model.AuthPrincipal;
import com.jame.dev.gymApp.features.auth.domain.model.CustomOAuth2User;
import com.jame.dev.gymApp.features.auth.domain.model.UserPrincipal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IdentityExtractorApplicationServiceTest {

   private final Authentication authentication = mock(Authentication.class);

   @InjectMocks
   private final IdentityExtractorApplicationService service = new IdentityExtractorApplicationService();

   @AfterEach
   void clearSecurityContext() {
      SecurityContextHolder.clearContext();
   }

   @Test
   @DisplayName("Should retrieve the username from principal object.")
   void returnUsernameFromPrincipal() {
      final UserPrincipal principal = UserPrincipal.builder()
         .id(1L)
         .username("john.doe")
         .password("encoded")
         .authorities(Collections.emptyList())
         .build();
      given(authentication.getPrincipal()).willReturn(principal);

      final String username = assertDoesNotThrow(() -> service.extract(authentication));

      assertEquals("john.doe", username);
      verify(authentication).getPrincipal();
      verifyNoMoreInteractions(authentication);
   }

   @Test
   @DisplayName("Should return AuthPrincipal object.")
   void returnsAuthPrincipalObject() {
      final UserPrincipal principal = UserPrincipal.builder()
         .id(1L)
         .username("john.doe")
         .password("encoded")
         .authorities(Collections.emptyList())
         .build();
      given(authentication.getPrincipal()).willReturn(principal);
      SecurityContextHolder.getContext().setAuthentication(authentication);

      final AuthPrincipal result = assertDoesNotThrow(service::getContextPrincipal);

      assertEquals(principal, result);
      verify(authentication).getPrincipal();
      verifyNoMoreInteractions(authentication);
   }

   @Test
   @DisplayName("Should throws InvalidAuthenticationPrincipalException when not instance of AuthPrincipal.")
   void throwsInvalidAuthenticationPrincipalException_when_is_not_instanceOf_AuthPrincipal() {
      given(authentication.getPrincipal()).willReturn("anonymous");

      assertThrowsExactly(InvalidAuthenticationPrincipalException.class,
         () -> service.extract(authentication));

      verify(authentication).getPrincipal();
      verifyNoMoreInteractions(authentication);
   }

   @Test
   @DisplayName("Should returns a CustomOauth2User object.")
   void returnsCustomOauth2User() {
      final CustomOAuth2User oauth = CustomOAuth2User.builder()
         .id(1L)
         .username("john.doe")
         .provider(AuthProvider.GOOGLE)
         .attributes(Collections.emptyMap())
         .authorities(Collections.emptyList())
         .build();
      given(authentication.getPrincipal()).willReturn(oauth);

      final CustomOAuth2User result = assertDoesNotThrow(() -> service.getOauthUser(authentication));

      assertEquals(oauth, result);
      verify(authentication).getPrincipal();
      verifyNoMoreInteractions(authentication);
   }

   @Test
   @DisplayName("Should throws InvalidAuthenticationPrincipalException when not instance of CustomOauth2User.")
   void throwsInvalidAuthenticationPrincipalException_when_is_not_instanceOf_CustomOauth2User() {
      final UserPrincipal principal = UserPrincipal.builder()
         .id(1L)
         .username("john.doe")
         .password("encoded")
         .authorities(Collections.emptyList())
         .build();
      given(authentication.getPrincipal()).willReturn(principal);

      assertThrowsExactly(InvalidAuthenticationPrincipalException.class,
         () -> service.getOauthUser(authentication));

      verify(authentication).getPrincipal();
      verifyNoMoreInteractions(authentication);
   }

   @Test
   @DisplayName("Should returns a UserPrincipal object.")
   void returnsUserPrincipal() {
      final UserPrincipal principal = UserPrincipal.builder()
         .id(1L)
         .username("john.doe")
         .password("encoded")
         .authorities(Collections.emptyList())
         .build();
      given(authentication.getPrincipal()).willReturn(principal);

      final UserPrincipal result = assertDoesNotThrow(() -> service.getUserPrincipal(authentication));

      assertEquals(principal, result);
      verify(authentication).getPrincipal();
      verifyNoMoreInteractions(authentication);
   }

   @Test
   @DisplayName("Should throws InvalidAuthenticationPrincipalException when not instance of UserPrincipal.")
   void throwsInvalidAuthenticationPrincipalException_when_is_not_instanceOf_UserPrincipal() {
      final CustomOAuth2User oauth = CustomOAuth2User.builder()
         .id(1L)
         .username("john.doe")
         .provider(AuthProvider.GOOGLE)
         .attributes(Collections.emptyMap())
         .authorities(Collections.emptyList())
         .build();
      given(authentication.getPrincipal()).willReturn(oauth);

      assertThrowsExactly(InvalidAuthenticationPrincipalException.class,
         () -> service.getUserPrincipal(authentication));

      verify(authentication).getPrincipal();
      verifyNoMoreInteractions(authentication);
   }
}