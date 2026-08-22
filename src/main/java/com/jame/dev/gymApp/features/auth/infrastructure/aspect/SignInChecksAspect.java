package com.jame.dev.gymApp.features.auth.infrastructure.aspect;

import com.jame.dev.gymApp.features.auth.api.request.SignInRequest;
import com.jame.dev.gymApp.features.auth.application.contract.AuthenticationChecksService;
import com.jame.dev.gymApp.features.auth.application.contract.verification.VerificationService;
import com.jame.dev.gymApp.features.auth.domain.exception.NonLocalAuthenticationAllowedException;
import com.jame.dev.gymApp.features.user.domain.exception.UserNotVerifiedException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Deprecated
@Aspect
@Component
@RequiredArgsConstructor
public class SignInChecksAspect {
   private final AuthenticationChecksService authenticationChecksService;
   private final VerificationService verificationService;

   @Before("@annotation(com.jame.dev.gymApp.features.auth.infrastructure.annotation.CheckSignIn) && args(dto, ..)")
   public void publishSignInValidation(final SignInRequest dto) {
      if (!verificationService.checkVerifiedDeactivated(dto.email()))
         throw new UserNotVerifiedException("This account is not verified.");

      if (!authenticationChecksService.isLocalProvider(dto.email())) {
         throw new NonLocalAuthenticationAllowedException("LOCAL Authentication isn't supported in this account.");
      }
   }
}
