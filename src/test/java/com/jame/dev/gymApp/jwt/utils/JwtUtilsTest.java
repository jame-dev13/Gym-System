package com.jame.dev.gymApp.jwt.utils;

import com.jame.dev.gymApp.exception.InvalidSignedJwtKeyException;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

   private JwtUtils jwtUtils;
   private final String VALID_SECRET = "hpBmH5UzH/u738cwN3IDBR7dUPtT151YZL6k7bznVHE=";
   private final String INVALID_SECRET = "INVALID_SECRET";
   private final Clock clock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);
   ;

   @BeforeEach
   void setUp() {
      this.jwtUtils = new JwtUtils(clock);
   }

   @Test
   @DisplayName("Signed key")
   void signWith() {
      ReflectionTestUtils.setField(jwtUtils, "secret", VALID_SECRET);
      Key signedKey = jwtUtils.signWith();
      assertNotNull(signedKey, "Key should not be null.");
      assertEquals("HmacSHA256", signedKey.getAlgorithm(), "It should have the same algorithm.");
   }

   @Test
   @DisplayName("Invalid Sign")
   void invalidSignWith() {
      ReflectionTestUtils.setField(jwtUtils, "secret", INVALID_SECRET);
      assertThrows(InvalidSignedJwtKeyException.class, jwtUtils::signWith, "Should throws an Exception.");
   }

   @Test
   @DisplayName("Token build.")
   void successfulTokenBuild() {
      ReflectionTestUtils.setField(jwtUtils, "secret", VALID_SECRET);
      String token = assertDoesNotThrow(() -> jwtUtils
                      .buildToken("Angel", 10_000L),
              "Should return the token object.");
      assertNotNull(token, "Token should not be null.");
      assertFalse(token.isEmpty(), "Token should not be empty.");
      assertFalse(token.isBlank(), "Token should not be blank.");
   }

   @Test
   @DisplayName("Fail built token with invalid secret.")
   void failureTokenBuild() {
      ReflectionTestUtils.setField(jwtUtils, "secret", INVALID_SECRET);
      assertThrows(InvalidSignedJwtKeyException.class, () -> jwtUtils.buildToken("Angel", 10_000L),
              "Should throwing an Exception.");
   }

   @Test
   @DisplayName("Returns claims.")
   void returnsClaim() {
      ReflectionTestUtils.setField(jwtUtils, "secret", VALID_SECRET);
      String token = jwtUtils.buildToken("Angel", 10_000L);
      Optional<String> optionalSubject = Assertions.assertDoesNotThrow(() -> jwtUtils.getClaim(token, Claims::getSubject),
              "Should return the Optional Object.");
      Assertions.assertNotNull(optionalSubject);
      assertFalse(optionalSubject.isEmpty());
      assertTrue(optionalSubject.isPresent());
   }

   @Test
   @DisplayName("Do not return claims on modifying token.")
   void tokenModifiedFails() {
      ReflectionTestUtils.setField(jwtUtils, "secret", VALID_SECRET);
      String token = jwtUtils.buildToken("Angel", 10_000L);
      String modifiedToken = token.concat("123");
      Optional<String> optionalSubject = jwtUtils.getClaim(modifiedToken, Claims::getSubject);
      assertTrue(optionalSubject.isEmpty(), "Should be empty.");
      assertFalse(optionalSubject.isPresent(), "Should not be present");
      assertEquals(Optional.empty(), optionalSubject, "Should be equal to Optional.empty()");
   }

   @Test
   @DisplayName("Token expires.")
   void tokenExpired() {

      Clock initialClock = Clock.fixed(
              Instant.parse("2026-01-01T00:00:00Z"),
              ZoneOffset.UTC
      );

      JwtUtils jwtUtils = new JwtUtils(initialClock);
      ReflectionTestUtils.setField(jwtUtils, "secret", VALID_SECRET);

      String token = jwtUtils.buildToken("Angel", 2000L);

      Clock laterClock = Clock.fixed(
              Instant.parse("2026-01-01T00:00:03Z"),
              ZoneOffset.UTC
      );

      jwtUtils = new JwtUtils(laterClock);
      ReflectionTestUtils.setField(jwtUtils, "secret", VALID_SECRET);

      Optional<String> optionalSubject =
              jwtUtils.getClaim(token, Claims::getSubject);

      assertTrue(optionalSubject.isEmpty());
   }

}