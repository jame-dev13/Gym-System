package com.jame.dev.gymApp.config.app;

import com.jame.dev.gymApp.entity.*;
import com.jame.dev.gymApp.model.dto.in.CustomerDtoInput;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
import com.jame.dev.gymApp.service.in.*;
import com.jame.dev.gymApp.shared.enums.AuthProvider;
import com.jame.dev.gymApp.shared.enums.Membership;
import com.jame.dev.gymApp.shared.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class InitConfig {

   @Value("${app.user.admin.email}")
   private String emailAdmin;

   @Value("${app.user.admin.password}")
   private String passwordAdmin;

   @Value("${app.user.email}")
   private String emailUser;

   @Value("${app.user.password}")
   private String passwordUser;


   @Bean
   public CommandLineRunner runnerInitUsersAndCustomers(final UserService userService,
                                                        final CustomerService customerService,
                                                        final VerificationService verificationService) {
      return args -> {
         final UserDtoInput admin = UserDtoInput.builder()
                 .name("admin")
                 .email(emailAdmin)
                 .password(passwordAdmin)
                 .roles(Set.of(Role.ADMIN, Role.USER))
                 .authProvider(AuthProvider.LOCAL)
                 .build();
         final UserDtoInput user = UserDtoInput.builder()
                 .name("user")
                 .email(emailUser)
                 .password(passwordUser)
                 .roles(Set.of(Role.USER))
                 .authProvider(AuthProvider.LOCAL)
                 .build();
         final UserEntity adminEntity = userService.save(admin);
         final VerificationEntity verificationAdmin = verificationService.save(adminEntity);
         verificationService.verify(adminEntity.getEmail(), verificationAdmin.getId());
         final UserEntity userEntity = userService.save(user);
         final VerificationEntity verificationUser = verificationService.save(userEntity);
         verificationService.verify(userEntity.getEmail(), verificationUser.getId());

         final CustomerDtoInput customerDtoInput = new CustomerDtoInput(userEntity.getId(), "1112223334");
         final CustomerEntity customer = customerService.save(customerDtoInput);
         log.info("User created. -> {}\n", userEntity);
         log.info("Customer created. -> {}\n", customer);
      };
   }

   @Bean
   public CommandLineRunner runnerMembershipsAndPrices(final MembershipService membershipService, final PricingService pricingService) {
      return args -> {
         final List<String> ORDER = List.of("biweekly", "monthly", "quarterly", "annual");
         final Map<String, BigDecimal> prices = Map.ofEntries(
                 Map.entry("biweekly", BigDecimal.valueOf(150.00d)),
                 Map.entry("monthly", BigDecimal.valueOf(300.00d)),
                 Map.entry("quarterly", BigDecimal.valueOf(900.00d)),
                 Map.entry("annual", BigDecimal.valueOf(3600.00d))
         );
         log.info("Prices set -> {}", prices);

         final Map<String, MemberShipEntity> memberships = new LinkedHashMap<>();
         ORDER.forEach(name -> {
            final Membership type = Membership.valueOf(name.toUpperCase());
            final MemberShipEntity entity = membershipService.save(new MemberShipEntity(null, type));
            memberships.put(name, entity);
         });

         log.info("Memberships created -> {}", memberships);

         ORDER.forEach(name -> {
            final MemberShipEntity membership = memberships.get(name);
            final BigDecimal price = prices.get(name);

            final PricingEntity pricingEntity =
                    pricingService.save(new PricingEntity(null, membership, price));
            log.info("Membership Prices -> {}: {}", name, pricingEntity);
         });
      };
   }
}
