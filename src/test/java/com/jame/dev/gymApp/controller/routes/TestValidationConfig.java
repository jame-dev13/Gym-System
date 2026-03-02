package com.jame.dev.gymApp.controller.routes;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@TestConfiguration
public class TestValidationConfig {

   @Bean
   public static LocalValidatorFactoryBean validator() {
      return new LocalValidatorFactoryBean();
   }

   @Bean
   public static MethodValidationPostProcessor methodValidationPostProcessor() {
      return new MethodValidationPostProcessor();
   }
}