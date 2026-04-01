package com.jame.dev.gymApp.aspects.imp.aspects;

import com.jame.dev.gymApp.exception.UserNotVerifiedException;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
import com.jame.dev.gymApp.service.in.AuthenticationChecksService;
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

   @Before("@annotation(com.jame.dev.gymApp.aspects.annotations.aspects.CheckExistence) && args(dto)")
   public void validateExistenceAndVerificationStatus(UserDtoInput dto) {
      if (authenticationChecksService.checkExistence(dto.email())) {
         throw new UserNotVerifiedException("This account is not verified yet.");
      }
   }
}
