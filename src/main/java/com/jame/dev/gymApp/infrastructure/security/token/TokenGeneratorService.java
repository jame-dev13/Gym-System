package com.jame.dev.gymApp.infrastructure.security.token;

public interface TokenGeneratorService {
   String generateToken();
   String generateTokenOneTimeToken();
}
