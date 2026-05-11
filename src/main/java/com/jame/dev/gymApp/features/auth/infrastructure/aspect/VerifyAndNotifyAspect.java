package com.jame.dev.gymApp.features.auth.infrastructure.aspect;

import com.jame.dev.gymApp.features.user.api.request.UserRequest;
import com.jame.dev.gymApp.features.user.api.response.UserResponse;
import com.jame.dev.gymApp.features.auth.domain.event.UserNotifiableEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class VerifyAndNotifyAspect {
   private final ApplicationEventPublisher applicationEventPublisher;

   @AfterReturning(
           pointcut = "@annotation(com.jame.dev.gymApp.app.auth.infrastructure.annotation.PublishVerifyAndNotifyUser) && args(userRequest, ..)",
           returning = "response"
   )
   public void afterPostUserFromAdmin(UserRequest userRequest, ResponseEntity<UserResponse> response) {
      boolean isNotifiable = response.getStatusCode().is2xxSuccessful();
      applicationEventPublisher.publishEvent(new UserNotifiableEvent(
         userRequest, isNotifiable
      ));
   }
}
