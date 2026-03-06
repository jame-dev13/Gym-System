package com.jame.dev.gymApp.service.in;

import com.jame.dev.gymApp.aspects.annotations.EmailValid;

public interface ExpirationService {
   void getMoreTimeFor(@EmailValid final String email);
}
