package com.jame.dev.gymApp.features.audit.application.support.helper;

import com.jame.dev.gymApp.features.audit.application.dto.AuditLogActor;
import com.jame.dev.gymApp.features.audit.application.model.AuditAuthenticationResultValue;
import com.jame.dev.gymApp.features.audit.application.model.AuditExecutionContext;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;
import com.jame.dev.gymApp.features.auth.domain.exception.AuthenticationNullException;
import com.jame.dev.gymApp.features.auth.domain.exception.InvalidAuthenticationPrincipalException;
import com.jame.dev.gymApp.features.auth.domain.model.CustomOAuth2User;
import com.jame.dev.gymApp.features.auth.domain.model.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

public final class ExtractAuditLogActorHelper {

   public static AuditLogActor extractLogActor(final AuditExecutionContext context) {
      if (context.getAnnotation().entityType() == AuditLogEntityType.AUTHENTICATION) {
         return switch (context.getAnnotation().action()) {
            case SIGN_IN, REGISTER, LOGOUT -> {
               final var result = (AuditAuthenticationResultValue) context.getResultValue();
               yield new AuditLogActor(result.userId(), result.performedBy());
            }
            default -> throw new IllegalArgumentException("Unmatching action.");
         };
      }

      final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      if (Objects.isNull(auth) || !auth.isAuthenticated()) {
         throw new AuthenticationNullException("Authentication required.");
      }
      final Object principal = auth.getPrincipal();
      if (principal instanceof UserPrincipal user) {
         return new AuditLogActor(user.id(), user.username());
      }
      if (principal instanceof CustomOAuth2User user) {;
         return new AuditLogActor(user.id(), user.username());
      }
      throw new InvalidAuthenticationPrincipalException("No mapping for auth principal subject present.");
   }

   private ExtractAuditLogActorHelper() {
   }
}
