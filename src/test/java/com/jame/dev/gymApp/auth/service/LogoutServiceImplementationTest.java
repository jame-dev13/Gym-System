package com.jame.dev.gymApp.auth.service;

import com.jame.dev.gymApp.cache.service.BlacklistService;
import com.jame.dev.gymApp.config.web.CookieHelper;
import com.jame.dev.gymApp.shared.enums.CookieNames;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class LogoutServiceImplementationTest {

   @Mock BlacklistService blacklistService;
   @Mock CookieHelper cookieHelper;
   @Mock HttpServletRequest request;
   @Mock HttpServletResponse response;

   @InjectMocks
   LogoutServiceImplementation service;

   @Test
   @DisplayName("Should successfully logout and blacklist refresh token when cookies are present")
   void logoutShouldSucceedWhenCookiesExist() {
      String refreshToken = "valid-refresh-token";
      Cookie jwtCookie = new Cookie(CookieNames.COOKIE_JWT_REFRESH.getValue(), refreshToken);
      Cookie otherCookie = new Cookie("other-cookie", "some-value");
      Cookie[] cookies = {jwtCookie, otherCookie};

      given(request.getCookies()).willReturn(cookies);

      assertDoesNotThrow(() -> service.logout(request, response));

      verify(blacklistService).blacklistToken(refreshToken);
      verify(cookieHelper).clearCookie(response, CookieNames.COOKIE_JWT_REFRESH.getValue());
      verify(cookieHelper).clearCookie(response, "other-cookie");
      verifyNoMoreInteractions(blacklistService, cookieHelper);
   }

   @Test
   @DisplayName("Should successfully clear cookies without blacklisting when refresh token is not present")
   void logoutShouldOnlyClearCookiesWhenNoRefreshTokenExists() {
      Cookie otherCookie = new Cookie("session-id", "12345");
      Cookie[] cookies = {otherCookie};

      given(request.getCookies()).willReturn(cookies);

      assertDoesNotThrow(() -> service.logout(request, response));

      verifyNoInteractions(blacklistService);
      verify(cookieHelper).clearCookie(response, "session-id");
   }

   @Test
   @DisplayName("Should throw IllegalArgumentException when request has no cookies")
   void logoutShouldThrowExceptionWhenCookiesAreNull() {
      given(request.getCookies()).willReturn(null);

      assertThrowsExactly(IllegalArgumentException.class, () -> service.logout(request, response));

      verifyNoInteractions(blacklistService, cookieHelper);
   }
}