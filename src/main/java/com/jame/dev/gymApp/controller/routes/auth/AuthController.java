package com.jame.dev.gymApp.controller.routes.auth;

import com.jame.dev.gymApp.auth.service.AuthService;
import com.jame.dev.gymApp.config.web.CookieHelper;
import com.jame.dev.gymApp.model.dto.auth.CookieResponseDto;
import com.jame.dev.gymApp.model.dto.auth.SignInDto;
import com.jame.dev.gymApp.model.dto.auth.SignInOkDto;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

   private final AuthService authService;
   private final CookieHelper cookieHelper;

   @PostMapping("/signUp")
   public ResponseEntity<Void> signUp(@Valid @RequestBody final UserDtoInput dto) throws ExecutionException, InterruptedException {
      authService.signUp(dto);
      return ResponseEntity.status(HttpStatus.CREATED).build();
   }

   @PostMapping("/signIn")
   public ResponseEntity<SignInOkDto> sigIn(
           @Valid
           @RequestBody final SignInDto dto) {
      log.info("[AUTH]: Hit signINn");
      final SignInOkDto response = authService.signIn(dto);
      log.info("{}", response);
      return ResponseEntity.ok()
              .contentType(MediaType.APPLICATION_JSON)
              .header(HttpHeaders.SET_COOKIE, cookieHelper.createAccessTokenCookie(response.access()).toString())
              .header(HttpHeaders.SET_COOKIE, cookieHelper.createRefreshTokenCookie(response.refresh()).toString())
              .body(response);
   }

   @PostMapping("/refresh")
   public ResponseEntity<Void> refresh(
           @Valid
           @CookieValue(name = "refresh") final String value) {
      if (Objects.isNull(value)) {
         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }
      final CookieResponseDto cookies = authService.refresh(value);
      return buildResponse(cookies.access(), cookies.refresh());
   }

   private ResponseEntity<Void> buildResponse(String access, String refresh) {
      return ResponseEntity.ok()
              .header(HttpHeaders.SET_COOKIE, cookieHelper.createAccessTokenCookie(access).toString())
              .header(HttpHeaders.SET_COOKIE, cookieHelper.createRefreshTokenCookie(refresh).toString())
              .build();
   }
}
