package com.jame.dev.gymApp.features.audit.application.support.strategy.crud;

import com.jame.dev.gymApp.features.audit.application.model.AuditExecutionContext;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogCrudPayload;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;
import com.jame.dev.gymApp.features.audit.infrastructure.audit_strategy.crud.AuditLogCrudEntityResolver;
import com.jame.dev.gymApp.features.customer.api.request.CustomerRequest;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuditLogCrudCustomerResolver implements AuditLogCrudEntityResolver {

   @Override
   public AuditLogEntityType entity() {
      return AuditLogEntityType.CUSTOMER;
   }

   @Override
   public AuditLogCrudPayload resolveUpdate(AuditExecutionContext ctx) {
      final var customerResponse = (CustomerResponse) ctx.getResultValue();
      final var customerRequest = (CustomerRequest) ctx.getInput();
      return AuditLogCrudPayload.builder()
         .before(
            Map.of(
               "customerEmail", customerRequest.email(),
               "customerPhone", customerRequest.contact() == null ? "NON_SET" : customerRequest.contact()))
         .after(Map.of(
            "customerEmail", customerResponse.customerEmail(),
            "customerPhone", customerResponse.contact()
         ))
         .build();
   }
}
