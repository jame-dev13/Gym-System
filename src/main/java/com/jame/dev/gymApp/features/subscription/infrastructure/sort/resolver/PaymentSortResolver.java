package com.jame.dev.gymApp.features.subscription.infrastructure.sort.resolver;


import com.jame.dev.gymApp.features.subscription.infrastructure.sort.model.PaymentSortProperty;
import com.jame.dev.gymApp.infrastructure.sort.SortPropertyResolver;
import com.jame.dev.gymApp.infrastructure.sort.SortResolver;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Component("paymentSortResolver")
public class PaymentSortResolver implements SortPropertyResolver {
   private static final Map<String, String> PROPERTY_MAP = Arrays.stream(
      PaymentSortProperty.values()
   ).collect(
      Collectors.toMap(
         PaymentSortProperty::getApiProperty,
         PaymentSortProperty::getEntityProperty
      )
   );

   @Override
   public Pageable resolve(Pageable pageable) {
      return SortResolver.resolveSortPropertiesFrom(pageable, PROPERTY_MAP);
   }
}
