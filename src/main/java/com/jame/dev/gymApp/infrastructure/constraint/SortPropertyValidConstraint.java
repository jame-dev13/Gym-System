package com.jame.dev.gymApp.infrastructure.constraint;

import com.jame.dev.gymApp.infrastructure.annotation.SortPropertyValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;

public class SortPropertyValidConstraint implements ConstraintValidator<SortPropertyValid, Pageable> {

   private static final Set<String> WHITE_LIST = Set.of(
      "id", "name", "email",
      "userEmail", "pricing", "finished"
   );

   private final Predicate<String> isInvalidProperty = prop -> !WHITE_LIST.contains(prop) && !prop.contains(".");

   @Override
   public boolean isValid(Pageable value, ConstraintValidatorContext context) {
      if(value == null) return true;

      return value.getSort()
         .stream()
         .map(Sort.Order::getProperty)
         .map(p -> p.toLowerCase(Locale.ROOT))
         .noneMatch(isInvalidProperty);
   }
}
