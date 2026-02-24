package com.jame.dev.gymApp.utils;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service
public class BeanUtils implements ApplicationContextAware {
   private static ApplicationContext context;

   @Override
   public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
      context = applicationContext;
   }

   public static ApplicationContext getContext() {
      return context;
   }
}
