package com.jame.dev.gymApp.aspects.imp;

import com.jame.dev.gymApp.exception.NonLocalAuthenticationAllowedException;
import com.jame.dev.gymApp.exception.UserNotFoundException;
import com.jame.dev.gymApp.exception.UserNotVerifiedException;
import com.jame.dev.gymApp.model.dto.auth.SignInDto;
import com.jame.dev.gymApp.service.in.AuthenticationChecksService;
import com.jame.dev.gymApp.service.in.VerificationService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class PublishCheckIntegrityAspect {
   private final AuthenticationChecksService authenticationChecksService;
   private final VerificationService verificationService;

   @Before("@annotation(com.jame.dev.gymApp.aspects.annotations.CheckSignIn) && args(dto, ..)")
   public void publishSignInValidation(final SignInDto dto) {
      if (!authenticationChecksService.userExists(dto.email())) {
         throw new UserNotFoundException("User not found.");
      }

      if (!verificationService.checkVerifiedDeactivated(dto.email()))
         throw new UserNotVerifiedException("This account is not verified.");

      if (!authenticationChecksService.isLocalProvider(dto.email())) {
         throw new NonLocalAuthenticationAllowedException("LOCAL Authentication isn't supported in this account.");
      }
   }
}
