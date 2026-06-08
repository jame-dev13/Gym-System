package com.jame.dev.gymApp.features.auth.infrastructure.aspect;

import com.jame.dev.gymApp.domain.exception.EventPublisherException;
import com.jame.dev.gymApp.features.auth.domain.event.UserNotifiableEvent;
import com.jame.dev.gymApp.features.user.api.request.UserRequest;
import com.jame.dev.gymApp.features.user.api.response.UserResponse;
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
      pointcut = "@annotation(com.jame.dev.gymApp.features.auth.infrastructure.annotation.PublishVerifyAndNotifyUser) && args(request, ..)",
      returning = "response"
   )
   public void afterPostUserFromAdmin(UserRequest request, ResponseEntity<UserResponse> response) {
      boolean isNotifiable = response.getStatusCode().is2xxSuccessful();
      if (!isNotifiable)
         throw new EventPublisherException("Cannot send notification to user for now.");
      applicationEventPublisher
         .publishEvent(new UserNotifiableEvent(request.email(), request.password()));
   }
}
