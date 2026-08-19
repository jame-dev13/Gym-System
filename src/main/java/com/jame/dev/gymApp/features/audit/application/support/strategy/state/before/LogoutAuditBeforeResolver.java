package com.jame.dev.gymApp.features.audit.application.support.strategy.state.before;

import com.jame.dev.gymApp.features.audit.application.dto.AuditLogActor;
import com.jame.dev.gymApp.features.audit.application.model.AuditAuthOperation;
import com.jame.dev.gymApp.features.audit.application.model.AuditAuthenticationResultValue;
import com.jame.dev.gymApp.features.audit.application.model.AuditExecutionContext;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.infrastructure.audit_strategy.AuditBeforeResolver;
import com.jame.dev.gymApp.features.audit.infrastructure.spel_evaluator.AuditLogExpressionEvaluator;
import com.jame.dev.gymApp.features.auth.application.contract.JwtService;
import com.jame.dev.gymApp.features.auth.application.model.CookieNames;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogoutAuditBeforeResolver implements AuditBeforeResolver {
   private final AuditLogExpressionEvaluator evaluator;
   private final JwtService jwts;

   @Override
   public AuditLogAction action() {
      return AuditLogAction.LOGOUT;
   }

   @Override
   public void resolve(AuditExecutionContext context) {
      final Object input = evaluator.evaluateAsObject(context.getAnnotation().input(), context.getParamNames(), context.getArgs());

      if (!(input instanceof HttpServletRequest request))
         throw new IllegalArgumentException("Resolved object unexpected.");

      final var cookieMap = Arrays.stream(request.getCookies())
         .collect(Collectors.toUnmodifiableMap(Cookie::getName, Cookie::getValue));

      final long actorId = jwts
         .extractUserId(
            cookieMap.getOrDefault(
               CookieNames.COOKIE_JWT_ACCESS.getValue(),
               CookieNames.COOKIE_JWT_REFRESH.getValue()))
         .orElseThrow();

      final String username = jwts.extractSubject(cookieMap.getOrDefault(
            CookieNames.COOKIE_JWT_ACCESS.getValue(),
            CookieNames.COOKIE_JWT_REFRESH.getValue()))
         .orElseThrow();

      context.setEntityId(actorId);
      context.setResultValue(
         AuditAuthenticationResultValue.builder()
            .userId(actorId)
            .performedBy(username)
            .operation(AuditAuthOperation.LOGOUT.getOp())
            .build()
      );
      context.setAuditLogActor(new AuditLogActor(actorId, username));
   }
}
