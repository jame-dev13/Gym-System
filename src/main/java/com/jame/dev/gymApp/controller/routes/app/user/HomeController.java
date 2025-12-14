package com.jame.dev.gymApp.controller.routes.app.user;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/users/home")
@PreAuthorize("hasRole('USER')")
public class HomeController {

   @GetMapping
   public String home(){
      return "Hello you're in home page";
   }
}
