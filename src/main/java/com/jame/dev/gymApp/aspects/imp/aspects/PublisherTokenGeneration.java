package com.jame.dev.gymApp.aspects.imp.aspects;

import com.jame.dev.gymApp.model.listeners.TokenGenerationEvent;
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
public class PublisherTokenGeneration {
   private final ApplicationEventPublisher applicationEventPublisher;

   @AfterReturning(
           pointcut = """
                   @annotation(com.jame.dev.gymApp.aspects.annotations.aspects.PublishTokenGeneration)
                   && args(email, ..)
                   """,
           returning = "response")
   public void publishReGenerationToken(
           String email,
           ResponseEntity<Void> response) {
      if (response.getStatusCode().is2xxSuccessful()) {
         applicationEventPublisher.publishEvent(new TokenGenerationEvent(email));
      }
   }
}
