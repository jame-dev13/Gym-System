package com.jame.dev.gymApp.features.audit.application.support.strategy.crud;

import com.jame.dev.gymApp.features.audit.application.model.AuditExecutionContext;
import com.jame.dev.gymApp.features.audit.domain.exception.AuditResolverException;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogCrudPayload;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;
import com.jame.dev.gymApp.features.audit.infrastructure.audit_strategy.crud.AuditLogCrudEntityResolver;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import com.jame.dev.gymApp.features.customer.infrastructure.audit.model.CustomerInputStateModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class AuditLogCrudCustomerResolver implements AuditLogCrudEntityResolver {

   @Override
   public AuditLogEntityType entity() {
      return AuditLogEntityType.CUSTOMER;
   }

   @Override
   public AuditLogCrudPayload resolveUpdate(AuditExecutionContext ctx) {
      if (!(ctx.getResultValue() instanceof CustomerResponse result) || (!(ctx.getInput() instanceof CustomerInputStateModel(
         long customerId, boolean isSub, boolean hasAddressInfoSettled
      )))) {
         throw new AuditResolverException("Result type mismatch.");
      }
      return AuditLogCrudPayload.builder()
         .before(
            Map.of(
               "customerId", customerId,
               "isSubscriber", isSub,
               "hasAddressInfoSettled", hasAddressInfoSettled
            )
         )
         .after(
            Map.of(
               "customerId", result.id(),
               "isSubscriber", result.isSubscriber(),
               "hasAddressInfoSettled", Stream
                  .of(result.addressInfo())
                  .noneMatch(Objects::isNull)
            )
         )
         .build();
   }
}
