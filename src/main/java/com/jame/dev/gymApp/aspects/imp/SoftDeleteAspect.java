package com.jame.dev.gymApp.aspects.imp;

import com.jame.dev.gymApp.aspects.annotations.DoNotFilter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.Session;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(1)
public class SoftDeleteAspect {

   @PersistenceContext
   private EntityManager entityManager;

   @Around("@annotation(doNotFilter)")
   public Object manageFilter(ProceedingJoinPoint proceedingJoinPoint, DoNotFilter doNotFilter) throws Throwable {
      final Session session = entityManager.unwrap(Session.class);
      final String filterName = doNotFilter.filterName();

      session.disableFilter(filterName);
      return proceedingJoinPoint.proceed();
   }
}
