package com.jame.dev.gymApp.infrastructure.aspect;

import com.jame.dev.gymApp.domain.exception.LockException;
import com.jame.dev.gymApp.infrastructure.security.lock.LockProcess;
import com.jame.dev.gymApp.infrastructure.security.lock.LockProcessExecutorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class LockLifeCycleAspect {

   private final LockProcessExecutorService lockProcessExecutorService;

   @Around(
      "@annotation(com.jame.dev.gymApp.infrastructure.security.lock.LockProcess)"
   )
   public Object mangerLockProcess(final ProceedingJoinPoint joinPoint) {

      final var signature = (MethodSignature) joinPoint.getSignature();
      final var method = signature.getMethod();
      final var annotation = method.getAnnotation(LockProcess.class);
      String processKey = "";

      if (annotation != null) {
         processKey = annotation.processKey().getKey();
      }

      if (lockProcessExecutorService.isLocked(processKey))
         throw new LockException("Process Already Locked, waiting for auto-releasing.");

      final Object result;

      try {
         lockProcessExecutorService.lock(processKey);
         result = joinPoint.proceed();
      } catch (Throwable th) {
         log.error("Exception occurs while trying to lock huge process {}", th.getMessage());
         throw new LockException("Exception trying to lock up a process: " + th.getMessage(), th);
      } finally {
         lockProcessExecutorService.releaseLock(processKey);
      }
      return result;
   }

}
