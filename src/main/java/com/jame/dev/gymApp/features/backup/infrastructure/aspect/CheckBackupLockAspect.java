package com.jame.dev.gymApp.features.backup.infrastructure.aspect;

import com.jame.dev.gymApp.domain.exception.LockException;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import com.jame.dev.gymApp.infrastructure.security.lock.LockKeys;
import com.jame.dev.gymApp.infrastructure.security.lock.LockProcessExecutorService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class CheckBackupLockAspect {

   private final LockProcessExecutorService lockProcessExecutorService;

   @Around("""
      @within(com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess) ||
      @annotation(com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess)
      """)
   public Object checkLockProcess(final ProceedingJoinPoint joinPoint) throws Throwable {
      final var signature = (MethodSignature) joinPoint.getSignature();
      final var method = signature.getMethod();

      var annotation = AnnotatedElementUtils.findMergedAnnotation(method, CheckLockProcess.class);

      if (annotation == null) {
         final var clazz = AopUtils.getTargetClass(joinPoint.getTarget());
         annotation = AnnotatedElementUtils.findMergedAnnotation(clazz, CheckLockProcess.class);
      }

      if (annotation == null) {
         return joinPoint.proceed();
      }

      for (final LockKeys key : annotation.keys()) {
         final String value = key.getKey();
         if (lockProcessExecutorService.isLocked(value)) {
            throw new LockException("Try again latter.");
         }
      }
      return joinPoint.proceed();
   }
}
