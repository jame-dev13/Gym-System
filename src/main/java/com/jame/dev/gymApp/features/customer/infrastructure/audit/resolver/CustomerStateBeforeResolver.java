package com.jame.dev.gymApp.features.customer.infrastructure.audit.resolver;

import com.jame.dev.gymApp.features.customer.domain.exception.CustomerNotFoundException;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerQueryRepository;
import com.jame.dev.gymApp.features.customer.infrastructure.audit.model.CustomerInputStateModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class CustomerStateBeforeResolver {
   private final CustomerQueryRepository customerQueryRepository;

   public CustomerInputStateModel resolveState(long customerId) {
      final var customer = customerQueryRepository.findById(customerId)
         .orElseThrow(CustomerNotFoundException::new);
      final boolean isSub = !customer.getSubscriptions().isEmpty();
      final boolean addressInfoStream = Stream
         .of(customer.getAddressInfo())
         .allMatch(Objects::nonNull);
      return new CustomerInputStateModel(customerId, isSub, addressInfoStream);
   }

}
