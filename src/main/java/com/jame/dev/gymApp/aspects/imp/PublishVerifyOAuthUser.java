package com.jame.dev.gymApp.aspects.imp;


import com.jame.dev.gymApp.oauth2.model.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class PublishVerifyOAuthUser {
   private final ApplicationEventPublisher applicationEventPublisher;

   @AfterReturning(
           pointcut = "@annotation(com.jame.dev.gymApp.aspects.annotations.VerifyOauthUser)",
           returning = "result")
   public void publishVerification(final Object result) {
      if (result instanceof CustomOAuth2User user) {
         applicationEventPublisher.publishEvent(user.getUser());
      }
   }
}
