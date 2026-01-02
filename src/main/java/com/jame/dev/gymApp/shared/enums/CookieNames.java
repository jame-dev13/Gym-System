package com.jame.dev.gymApp.shared.enums;

import lombok.Getter;

@Getter
public enum CookieNames {
   COOKIE_JWT_ACCESS("access"),
   COOKIE_JWT_REFRESH("refresh");
   private final String value;
   CookieNames(final String value){
      this.value = value;
   }
}
