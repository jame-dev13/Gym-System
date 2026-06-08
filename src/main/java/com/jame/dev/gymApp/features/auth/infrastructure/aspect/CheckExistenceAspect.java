package com.jame.dev.gymApp.features.auth.infrastructure.aspect;

import com.jame.dev.gymApp.features.auth.api.request.RegisterRequest;
import com.jame.dev.gymApp.features.auth.application.contract.AuthenticationChecksService;
import com.jame.dev.gymApp.features.user.domain.exception.UserNotVerifiedException;
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

   @Before("@annotation(com.jame.dev.gymApp.features.auth.infrastructure.annotation.CheckExistence) && args(dto)")
   public void validateExistenceAndVerificationStatus(RegisterRequest dto) {
      if (authenticationChecksService.checkExistence(dto.email())) {
         throw new UserNotVerifiedException("This account is not verified yet.");
      }
      log.info("Check Passed.");
   }
}
