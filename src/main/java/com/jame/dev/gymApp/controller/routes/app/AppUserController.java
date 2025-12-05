package com.jame.dev.gymApp.controller.routes.app;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${app.mapping.user}")
@PreAuthorize("hasRole('USER')")
public class AppUserController {
}
