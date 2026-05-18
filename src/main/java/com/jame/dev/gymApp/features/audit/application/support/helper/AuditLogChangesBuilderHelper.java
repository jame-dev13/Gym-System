package com.jame.dev.gymApp.features.audit.application.support.helper;

import com.jame.dev.gymApp.features.audit.application.dto.AuditLogChanges;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.customer.api.request.CustomerRequest;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionRequest;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.features.user.api.request.UserRequest;
import com.jame.dev.gymApp.features.user.api.response.UserResponse;

import java.util.Map;

public class AuditLogChangesBuilderHelper {

   public static AuditLogChanges buildChanges(AuditLogAction action, long entityId, Object input, Object response) {
      return switch (action) {
         case INSERT -> generateAuditLogByType(true, entityId, input, response);
         case UPDATE -> generateAuditLogByType(false, entityId, input, response);
         case RECOVER -> new AuditLogChanges(
            Map.of("active", false),
            Map.of("entityId", entityId, "active", true)
         );
         case DELETE -> new AuditLogChanges(
            Map.of("entityId", entityId, "active", true),
            Map.of("active", false)
         );
         case HARD_DELETE -> new AuditLogChanges(
            Map.of("entityId", entityId),
            Map.of("status", "PERMANENTLY_DELETED.")
         );
         case UNKNOW -> throw new IllegalArgumentException("No changes build case defined for: " + action);
      };
   }

   private static AuditLogChanges generateAuditLogByType(boolean isInsert, long entityId, Object input, Object result) {
      if (input instanceof UserRequest userRequest && result instanceof UserResponse userResponse) {
         var changesMap = ChangeModelResolverHelper.buildChangesModelUser(entityId, userRequest, userResponse);
         return isInsert ?
            new AuditLogChanges(null, changesMap.get("after")) :
            new AuditLogChanges(changesMap.get("before"), changesMap.get("after"));
      }
      if (input instanceof CustomerRequest customerRequest && result instanceof CustomerResponse customerResponse) {
         var changesMap = ChangeModelResolverHelper.buildChangesModelCustomer(entityId, customerRequest, customerResponse);
         return isInsert ?
            new AuditLogChanges(null, changesMap.get("after")) :
            new AuditLogChanges(changesMap.get("before"), changesMap.get("after"));
      }
      if (input instanceof SubscriptionRequest subscriptionRequest && result instanceof SubscriptionResponse subscriptionResponse) {
         var changesMap = ChangeModelResolverHelper.buildChangesModelSubscription(entityId, subscriptionRequest, subscriptionResponse);
         return isInsert ?
            new AuditLogChanges(null, changesMap.get("after")) :
            new AuditLogChanges(changesMap.get("before"), changesMap.get("after"));
      }
      throw new IllegalArgumentException("Cannot resolve types: " + input.getClass() + ", " + result.getClass());
   }

}
