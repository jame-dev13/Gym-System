package com.jame.dev.gymApp.controller.routes.auth;

import com.jame.dev.gymApp.auth.service.AuthService;
import com.jame.dev.gymApp.model.dto.auth.CookieResponseDto;
import com.jame.dev.gymApp.model.dto.auth.SignInDto;
import com.jame.dev.gymApp.model.dto.auth.SignInOkDto;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
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

   @PostMapping("/signUp")
   public ResponseEntity<Void> signUp(@RequestBody final UserDtoInput dto) throws ExecutionException, InterruptedException {
      authService.signUp(dto);
      return ResponseEntity.status(HttpStatus.CREATED).build();
   }

   @PostMapping("/signIn")
   public ResponseEntity<SignInOkDto> sigIn(@RequestBody final SignInDto dto) {
      log.info("[AUTH]: Hit signINn");
      final SignInOkDto response = authService.signIn(dto);
      log.info("{}", response);
      return ResponseEntity.ok()
              .contentType(MediaType.APPLICATION_JSON)
              .header(HttpHeaders.SET_COOKIE, response.access())
              .header(HttpHeaders.SET_COOKIE, response.access())
              .body(response);
   }

   @PostMapping("/refresh")
   public ResponseEntity<Void> refresh(@CookieValue(name = "_HOST-JWT_REFRESH") final String value) {
      if (Objects.isNull(value)) {
         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }
      final CookieResponseDto cookies = authService.refresh(value);
      return buildResponse(cookies.access(), cookies.refresh());
   }

   private ResponseEntity<Void> buildResponse(String access, String refresh) {
      return ResponseEntity.ok()
              .header(HttpHeaders.SET_COOKIE, access)
              .header(HttpHeaders.SET_COOKIE, refresh)
              .build();
   }
}
