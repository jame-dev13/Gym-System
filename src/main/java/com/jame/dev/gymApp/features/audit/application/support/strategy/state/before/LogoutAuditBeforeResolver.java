package com.jame.dev.gymApp.features.audit.application.support.strategy.state.before;

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
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogoutAuditBeforeResolver implements AuditBeforeResolver {
   private final JwtService jwts;
   private final AuditLogExpressionEvaluator evaluator;

   @Override
   public AuditLogAction action() {
      return AuditLogAction.LOGOUT;
   }

   @Override
   public void resolve(AuditExecutionContext context) {
      final var input = (HttpServletRequest) evaluator.evaluateAsObject(context.getAnnotation().input(), context.getParamNames(), context.getArgs());
      context.setInput(input);
      final var accessValue = Arrays.stream(input.getCookies())
         .filter(c -> Objects.nonNull(c) && c.getName().equals(CookieNames.COOKIE_JWT_ACCESS.getValue()))
         .findFirst()
         .map(Cookie::getValue)
         .orElseThrow(() -> new IllegalArgumentException("No access cookie value present."));
      final Long userId = jwts.extractUserId(accessValue)
         .orElse(null);
      final var username = jwts.extractSubject(accessValue)
         .orElse("");

      context.setEntityId(userId);

      context.setResultValue(new AuditAuthenticationResultValue(userId, username, AuditAuthOperation.LOGOUT.getOp()));
   }
}
