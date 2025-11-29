package com.jame.dev.gymApp.auth.service;

import jakarta.servlet.http.HttpServletRequest;

public interface LogoutService {
   void logout(HttpServletRequest request);
}
