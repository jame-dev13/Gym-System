package com.jame.dev.gymApp.service;

import com.jame.dev.gymApp.service.in.TokenGeneratorService;
import com.jame.dev.gymApp.service.out.TokenGeneratorImplementation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.*;

public class TokenGeneratorServiceTest {

   private final TokenGeneratorService service = new TokenGeneratorImplementation();

   @DisplayName("Generates token of 6 character alphanumerics")
   @RepeatedTest(value = 10, name = "generateToken method rep: {currentRepetition} of {totalRepetitions}")
   void generateToken(){
      String token = service.generateToken();
      assertAll("Generates a token non-null of 6 characters alphanumeric",
              () -> assertNotNull(token, "Should not be null."),
              () -> assertSame(6, token.length(), "The length should be 6."),
              () -> assertTrue(token.chars().allMatch(Character::isLetterOrDigit),
                      "Should only contains Letters or digits.")
      );
   }
}
