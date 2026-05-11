package com.jame.dev.gymApp.features.auth.application.contract;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface LogoutService {
   void logout(HttpServletRequest request, HttpServletResponse response);
}
