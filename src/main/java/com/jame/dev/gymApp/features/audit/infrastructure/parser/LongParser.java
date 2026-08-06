package com.jame.dev.gymApp.features.audit.infrastructure.parser;

import org.springframework.stereotype.Component;

@Component
public class LongParser {

   public Long parseString(String longStr) {
      try {
         return Long.parseLong(longStr);
      } catch (NumberFormatException e) {
         throw new RuntimeException("Cannot parse: " + longStr, e);
      }
   }
}
