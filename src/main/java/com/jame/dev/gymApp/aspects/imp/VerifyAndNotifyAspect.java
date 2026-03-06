package com.jame.dev.gymApp.aspects.imp;

import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
import com.jame.dev.gymApp.model.dto.out.UserDtoOutput;
import com.jame.dev.gymApp.model.listeners.UserNotifiable;
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
           pointcut = "@annotation(com.jame.dev.gymApp.aspects.annotations.PublishVerifyAndNotifyUser) && args(userDtoInput, ..)",
           returning = "response"
   )
   public void afterPostUserFromAdmin(UserDtoInput userDtoInput, ResponseEntity<UserDtoOutput> response) {
      boolean isNotifiable = response.getStatusCode().is2xxSuccessful();
      applicationEventPublisher.publishEvent(new UserNotifiable(
              userDtoInput, isNotifiable
      ));
   }
}
