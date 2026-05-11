package com.jame.dev.gymApp.features.customer.application.support.updater;

import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import com.jame.dev.gymApp.features.customer.api.request.CustomerRequest;
import com.jame.dev.gymApp.features.customer.application.contract.CustomerUpdater;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class CustomerApplicationUpdater implements CustomerUpdater {

   @Override
   public void apply(final CustomerEntity customer, final CustomerRequest dto) {
      final UserEntity user = customer.getUser();
      user.setEmail(dto.email());
      customer.setPhoneContact(dto.contact());
      customer.setUpdatedAt(Instant.now());
   }
}
