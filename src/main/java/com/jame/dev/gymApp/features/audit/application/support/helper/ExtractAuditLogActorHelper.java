package com.jame.dev.gymApp.features.audit.application.support.helper;

import com.jame.dev.gymApp.features.audit.application.dto.AuditLogActor;
import com.jame.dev.gymApp.features.auth.domain.exception.AuthenticationNullException;
import com.jame.dev.gymApp.features.auth.domain.exception.InvalidAuthenticationPrincipalException;
import com.jame.dev.gymApp.features.auth.domain.model.AuthenticatedUser;
import com.jame.dev.gymApp.features.auth.domain.model.CustomOAuth2User;
import com.jame.dev.gymApp.features.auth.domain.model.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

public class ExtractAuditLogActorHelper {

   public static AuditLogActor extractLogActor() {
      final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      if(Objects.isNull(auth) || !auth.isAuthenticated())
         throw new AuthenticationNullException("Authentication required.");
      final Object principal = auth.getPrincipal();
      if (principal instanceof UserPrincipal user) {
         return new AuditLogActor(user.getId(), user.getUsername());
      }
      if (principal instanceof CustomOAuth2User oauthUser) {
         final AuthenticatedUser user = oauthUser.getUser();
         return new AuditLogActor(user.id(), user.email());
      }
      throw new InvalidAuthenticationPrincipalException("No mapping for auth principal subject present.");
   }
}
