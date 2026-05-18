package com.jame.dev.gymApp.features.audit.application.support.factory;

import com.jame.dev.gymApp.features.audit.application.dto.AuditLogChanges;
import com.jame.dev.gymApp.features.audit.application.support.helper.AuditLogChangesBuilderHelper;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;

public class AuditLogChangesFactory {

   public static AuditLogChanges createAuditLogChangesFrom(AuditLogAction action, long entityId, Object input, Object output) {
      return AuditLogChangesBuilderHelper.buildChanges(action, entityId, input, output);
   }
}
