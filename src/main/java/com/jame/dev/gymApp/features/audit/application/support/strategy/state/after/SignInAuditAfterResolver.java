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
import com.jame.dev.gymApp.features.auth.domain.exception.AuthenticationNullException;
import com.jame.dev.gymApp.features.auth.domain.model.AuthenticatedUser;
import com.jame.dev.gymApp.features.auth.domain.model.CustomOAuth2User;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor
public class SignInAuditAfterResolver implements AuditAfterResolver {
   private final AuditLogExpressionEvaluator evaluator;
   private final JwtService jwts;

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
            final AuthenticatedUser user = getAuthUser.apply(auth);
            yield AuditAuthenticationResultValue.builder()
               .userId(user.id())
               .performedBy(user.email())
               .operation(AuditAuthOperation.OAUTH_SIGN_IN.getOp() + " with " + user.authProvider().getProvider())
               .build();
         }
         default -> throw new IllegalStateException("Unexpected value: " + input);
      };

      context.setEntityId(resultValue.userId());
      context.setResultValue(resultValue);
   }

   private static final Function<@NonNull Authentication, AuthenticatedUser> getAuthUser = auth -> {
      if (auth instanceof AnonymousAuthenticationToken) {
         throw new AuthenticationNullException("Authentication token is undefined.");
      }

      final var principal = Objects.requireNonNull(auth.getPrincipal(), "User principal is undefined.");

      if (!(principal instanceof CustomOAuth2User oauth)) {
         throw new IllegalArgumentException("Unexpecting type." + principal);
      }

      return Objects.requireNonNull(oauth.getUser(), "Authenticated user is undefined.");
   };
}
