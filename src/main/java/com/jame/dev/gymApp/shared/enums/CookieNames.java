package com.jame.dev.gymApp.shared.enums;

import lombok.Getter;

@Getter
public enum CookieNames {
   COOKIE_JWT_ACCESS("__HOST-BMOSESSIONID-JWT_ACCESS"),
   COOKIE_JWT_REFRESH("__HOST-BMOSESSIONID-JWT_REFRESH");
   private final String value;
   CookieNames(final String value){
      this.value = value;
   }
}
