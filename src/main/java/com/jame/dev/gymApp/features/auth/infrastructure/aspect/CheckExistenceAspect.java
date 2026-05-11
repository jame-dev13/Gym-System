package com.jame.dev.gymApp.features.auth.infrastructure.aspect;

import com.jame.dev.gymApp.features.user.domain.exception.UserNotVerifiedException;
import com.jame.dev.gymApp.features.user.api.request.UserRequest;
import com.jame.dev.gymApp.features.auth.application.contract.AuthenticationChecksService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class CheckExistenceAspect {
   private final AuthenticationChecksService authenticationChecksService;

   @Before("@annotation(com.jame.dev.gymApp.app.auth.infrastructure.annotation.CheckExistence) && args(dto)")
   public void validateExistenceAndVerificationStatus(UserRequest dto) {
      if (authenticationChecksService.checkExistence(dto.email())) {
         throw new UserNotVerifiedException("This account is not verified yet.");
      }
   }
}
