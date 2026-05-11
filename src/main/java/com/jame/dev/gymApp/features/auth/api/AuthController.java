package com.jame.dev.gymApp.features.auth.api;

import com.jame.dev.gymApp.features.auth.infrastructure.annotation.PublishVerify;
import com.jame.dev.gymApp.infrastructure.annotation.NotEmptyNull;
import com.jame.dev.gymApp.features.auth.application.contract.AuthService;
import com.jame.dev.gymApp.infrastructure.config.web.CookieHelper;
import com.jame.dev.gymApp.features.auth.api.response.CookieResponse;
import com.jame.dev.gymApp.features.auth.api.request.SignInRequest;
import com.jame.dev.gymApp.features.auth.api.response.SignInResponse;
import com.jame.dev.gymApp.features.user.api.request.UserRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

   private final AuthService authService;
   private final CookieHelper cookieHelper;

   @PostMapping("/signUp")
   @PublishVerify
   public ResponseEntity<Void> signUp(
           @Valid
           @RequestBody final UserRequest user) {
      authService.signUp(user);
      return ResponseEntity.status(HttpStatus.CREATED).build();
   }

   @PostMapping("/signIn")
   public ResponseEntity<SignInResponse> sigIn(
           @Valid
           @RequestBody final SignInRequest dto) {
      final SignInResponse response = authService.signIn(dto);
      return ResponseEntity.ok()
              .contentType(MediaType.APPLICATION_JSON)
              .header(HttpHeaders.SET_COOKIE, cookieHelper.createAccessTokenCookie(response.access()).toString())
              .header(HttpHeaders.SET_COOKIE, cookieHelper.createRefreshTokenCookie(response.refresh()).toString())
              .body(response);
   }

   @PostMapping("/refresh")
   public ResponseEntity<Void> refresh(
           @Valid
           @CookieValue(name = "refresh")
           @NotEmptyNull final String value) {
      final CookieResponse cookies = authService.refresh(value);
      return buildResponse(cookies.access(), cookies.refresh());
   }

   private ResponseEntity<Void> buildResponse(String access, String refresh) {
      return ResponseEntity.ok()
              .header(HttpHeaders.SET_COOKIE, cookieHelper.createAccessTokenCookie(access).toString())
              .header(HttpHeaders.SET_COOKIE, cookieHelper.createRefreshTokenCookie(refresh).toString())
              .build();
   }
}
