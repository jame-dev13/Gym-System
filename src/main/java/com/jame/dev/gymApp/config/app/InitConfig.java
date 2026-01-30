package com.jame.dev.gymApp.config.app;

import com.jame.dev.gymApp.entity.*;
import com.jame.dev.gymApp.model.dto.in.CustomerDtoInput;
import com.jame.dev.gymApp.model.dto.in.SubscriptionDtoInput;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
import com.jame.dev.gymApp.service.in.*;
import com.jame.dev.gymApp.shared.enums.AuthProvider;
import com.jame.dev.gymApp.shared.enums.Membership;
import com.jame.dev.gymApp.shared.enums.Role;
import jakarta.annotation.Priority;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.IntStream;

import static com.jame.dev.gymApp.shared.enums.Membership.*;

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

   private final Random random = new Random();

   @Bean
   @Priority(1)
   public CommandLineRunner runnerInitUsersAndCustomers(final UserService userService,
                                                        final CustomerService customerService,
                                                        final VerificationService verificationService) {
      return args -> {
         log.info("Runner InitUsersAndCustomers start execution.");
         final UserDtoInput admin = UserDtoInput.builder()
                 .name("admin")
                 .email(emailAdmin)
                 .password(passwordAdmin)
                 .roles(Set.of(Role.ADMIN))
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

         final CustomerDtoInput customerDtoInput = new CustomerDtoInput(userEntity.getEmail(), "1112223334");
         customerService.save(customerDtoInput);

         log.info("Runner InitUsersAndCustomers end execution.");
      };
   }

   @Bean
   @Priority(2)
   public CommandLineRunner runnerMembershipsAndPrices(final MembershipService membershipService, final PricingService pricingService) {
      return args -> {
         log.info("Runner MembershipAndPrices start execution.");
         final List<String> ORDER = List.of("biweekly", "monthly", "quarterly", "annual");
         final Map<String, BigDecimal> prices = Map.ofEntries(
                 Map.entry("biweekly", BigDecimal.valueOf(150.00d)),
                 Map.entry("monthly", BigDecimal.valueOf(300.00d)),
                 Map.entry("quarterly", BigDecimal.valueOf(900.00d)),
                 Map.entry("annual", BigDecimal.valueOf(3600.00d))
         );

         final Map<String, MemberShipEntity> memberships = new LinkedHashMap<>();
         ORDER.forEach(name -> {
            final Membership type = Membership.valueOf(name.toUpperCase());
            final MemberShipEntity entity = membershipService.save(new MemberShipEntity(null, type));
            memberships.put(name, entity);
         });

         ORDER.forEach(name -> {
            final MemberShipEntity membership = memberships.get(name);
            final BigDecimal price = prices.get(name);

            pricingService.save(new PricingEntity(null, membership, price));
         });
         log.info("Runner MembershipAndPrices end execution.");
      };
   }

   @Bean
   @Priority(3)
   public CommandLineRunner runnerCreationOfUsersCustomersAndSubscriptions(
           final UserService userService,
           final VerificationService verificationService,
           final CustomerService customerService,
           final SubscriptionService subscriptionService
   ) {
      return args -> {
         log.info("Runner CreationOfUsersCustomersAndSubscriptions start execution.");
         IntStream.range(0, 20)
                 .forEach(i -> {
                    //Users
                    final UserDtoInput userDto = createUser(i + 1);
                    final UserEntity userEntity = userService.save(userDto);
                    saveAndVerifyUser(verificationService, userEntity);
                    //Customers
                    final CustomerDtoInput customerDto = createCustomer(userEntity.getEmail(), i + 1);
                    final CustomerEntity customer = customerService.save(customerDto);
                    //Subscriptions
                    final SubscriptionDtoInput subDto = createSubscription(customer.getUser().getEmail());
                    subscriptionService.save(subDto);
                 });
         log.info("Runner CreationOfUsersCustomersAndSubscriptions end execution.");
      };
   }

   private UserDtoInput createUser(final int i) {
      return UserDtoInput.builder()
              .name("user" + i)
              .email("user" + i + "@mail.com")
              .password("password" + i)
              .authProvider(AuthProvider.LOCAL)
              .roles(Set.of(Role.USER))
              .build();
   }

   private CustomerDtoInput createCustomer(final String email, final int i) {
      return new CustomerDtoInput(email, "1234567" + i);
   }

   private SubscriptionDtoInput createSubscription(final String email) {
      final Membership[] memberships = {BIWEEKLY, MONTHLY, QUARTERLY, ANNUAL};
      final int randomIdx = random.nextInt(0, memberships.length - 1);
      return new SubscriptionDtoInput(email, memberships[randomIdx]);
   }

   private void saveAndVerifyUser(final VerificationService verificationService,
                                  final UserEntity user) {
      final var userVerification = verificationService.save(user);
      verificationService.verify(user.getEmail(), userVerification.getId());
   }
}
