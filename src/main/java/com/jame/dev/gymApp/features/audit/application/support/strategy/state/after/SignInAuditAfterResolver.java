package com.jame.dev.gymApp.features.audit.application.support.strategy.state.after;

import com.jame.dev.gymApp.features.audit.application.model.AuditAuthOperation;
import com.jame.dev.gymApp.features.audit.application.model.AuditAuthenticationResultValue;
import com.jame.dev.gymApp.features.audit.application.model.AuditExecutionContext;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.infrastructure.audit_strategy.AuditAfterResolver;
import com.jame.dev.gymApp.features.audit.infrastructure.spel_evaluator.AuditLogExpressionEvaluator;
import com.jame.dev.gymApp.features.auth.api.request.SignInRequest;
import com.jame.dev.gymApp.features.auth.api.response.SignInResponse;
import com.jame.dev.gymApp.features.auth.application.contract.JwtService;
import com.jame.dev.gymApp.features.auth.infrastructure.security.identity.IdentityExtractorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SignInAuditAfterResolver implements AuditAfterResolver {
   private final AuditLogExpressionEvaluator evaluator;
   private final JwtService jwts;
   private final IdentityExtractorService identityExtractorService;

   @Override
   public AuditLogAction action() {
      return AuditLogAction.SIGN_IN;
   }

   @Override
   public void resolve(AuditExecutionContext context) {
      final var input = context.getInput();
      final var resultValue = switch (input) {
         case SignInRequest ignored -> {
            final var result = evaluator.evaluateAsObject(context.getAnnotation().result(), context.getResult());
            final var res = (SignInResponse) result;
            final Long userId = jwts.extractUserId(res.access())
               .orElse(null);
            yield AuditAuthenticationResultValue.builder()
               .userId(userId)
               .performedBy(res.email())
               .operation(AuditAuthOperation.SIGN_IN.getOp())
               .build();
         }
         case Authentication auth -> {
            final var principal = identityExtractorService.getOauthUser(auth);
            yield AuditAuthenticationResultValue.builder()
               .userId(principal.id())
               .performedBy(principal.username())
               .operation(AuditAuthOperation.OAUTH_SIGN_IN.getOp() + " with " + principal.provider().getProvider())
               .build();
         }
         default -> throw new IllegalStateException("Unexpected value: " + input);
      };

      context.setEntityId(resultValue.userId());
      context.setResultValue(resultValue);
   }
}
