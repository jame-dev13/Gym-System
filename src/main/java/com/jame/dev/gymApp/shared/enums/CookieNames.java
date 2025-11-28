package com.jame.dev.gymApp.shared.enums;

import lombok.Getter;

@Getter
public enum CookieNames {
   COOKIE_JWT_ACCESS("_HOST-JWT_ACCESS"),
   COOKIE_JWT_REFRESH("_HOST-JWT_REFRESH");
   private final String value;
   CookieNames(final String value){
      this.value = value;
   }
}
