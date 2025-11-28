package com.jame.dev.gymApp.controller.routes.auth;

import com.jame.dev.gymApp.auth.service.AuthService;
import com.jame.dev.gymApp.model.dto.auth.CookieResponseDto;
import com.jame.dev.gymApp.model.dto.auth.SignInDto;
import com.jame.dev.gymApp.model.dto.auth.SignUpDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("${app.mapping.auth}")
@RequiredArgsConstructor
public class AuthController {

   private final AuthService authService;

   @PostMapping("/signUp")
   public ResponseEntity<?> signUp(@RequestBody final SignUpDto dto){
      return null;
   }

   @PostMapping("/signIn")
   public ResponseEntity<Void> sigIn(@RequestBody final SignInDto dto){
      CookieResponseDto cookies = authService.signIn(dto);
      return ResponseEntity
              .ok()
              .header(HttpHeaders.SET_COOKIE, cookies.access().toString())
              .header(HttpHeaders.SET_COOKIE, cookies.refresh().toString())
              .build();
   }

   @PostMapping("/refresh")
   public ResponseEntity<Void> refresh(@CookieValue(name = "_HOST-JWT_REFRESH") final String value){
      if(Objects.isNull(value)){
         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }
      CookieResponseDto cookies = authService.refresh(value);
      return ResponseEntity
              .ok()
              .header(HttpHeaders.SET_COOKIE, cookies.access().toString())
              .header(HttpHeaders.SET_COOKIE, cookies.refresh().toString())
              .build();
   }
}
