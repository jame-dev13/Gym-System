package com.jame.dev.gymApp.controller.routes.auth.signUp;

import com.jame.dev.gymApp.auth.filters.CustomAuthorizationFilter;
import com.jame.dev.gymApp.auth.service.AuthService;
import com.jame.dev.gymApp.config.web.CookieHelper;
import com.jame.dev.gymApp.controller.advice.ApiErrorResponseFactory;
import com.jame.dev.gymApp.controller.routes.auth.AuthController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
        controllers = AuthController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        classes = CustomAuthorizationFilter.class,
                        type = FilterType.ASSIGNABLE_TYPE)
        })
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerSignUpTest {

   @Autowired
   private MockMvc mockMvc;

   @Autowired
   private AuthController authController;

   @MockitoBean
   private AuthService authService;

   @MockitoBean
   private ApiErrorResponseFactory responseFactory;

   @MockitoBean
   private CookieHelper cookieHelper;


   @Test
   @DisplayName("[POST: 200 OK]: signUp successfully")
   void signUpSuccessfully(){

   }
}
