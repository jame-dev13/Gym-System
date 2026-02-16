package com.jame.dev.gymApp.aspects.imp;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Session;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(1)
public class FilterAspect {

   @PersistenceContext
   private EntityManager entityManager;

   @Before("execution(* org.springframework.data.repository.Repository+.*(..))")
   public void enableFilter() {
      final Session session = entityManager.unwrap(Session.class);
      session.enableFilter("deletedFilter").setParameter("active", true);
   }

}
