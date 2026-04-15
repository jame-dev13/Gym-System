package com.jame.dev.gymApp.config.app;

import com.jame.dev.gymApp.entity.MemberShipEntity;
import com.jame.dev.gymApp.entity.PricingEntity;
import com.jame.dev.gymApp.model.dto.in.CustomerDtoInput;
import com.jame.dev.gymApp.model.dto.in.SubscriptionDtoInput;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
import com.jame.dev.gymApp.model.dto.out.CustomerDtoOutput;
import com.jame.dev.gymApp.model.dto.out.UserDtoOutput;
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
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.IntStream;

import static com.jame.dev.gymApp.shared.enums.Membership.*;

@Slf4j
@Profile("dev")
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
                                                        final TokenGeneratorService tokenService,
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
         final UserDtoOutput adminSaved = userService.save(admin);
         saveAndVerifyUser(tokenService, verificationService, adminSaved.id());

         final UserDtoOutput userSaved = userService.save(user);
         saveAndVerifyUser(tokenService, verificationService, userSaved.id());

         final CustomerDtoInput customerDtoInput = new CustomerDtoInput(userSaved.email(), "1112223334");
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
           final TokenGeneratorService tokenGeneratorService,
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
                    final UserDtoOutput userEntity = userService.save(userDto);
                    saveAndVerifyUser(tokenGeneratorService, verificationService, userEntity.id());
                    //Customers
                    final CustomerDtoInput customerDto = createCustomer(userEntity.email(), i + 1);
                    final CustomerDtoOutput customer = customerService.save(customerDto);
                    //Subscriptions
                    final SubscriptionDtoInput subDto = createSubscription(customer.user().email());
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
      final int randomIdx = random.nextInt(0, memberships.length);
      return new SubscriptionDtoInput(email, memberships[randomIdx]);
   }

   private void saveAndVerifyUser(
           final TokenGeneratorService tokenGeneratorService,
           final VerificationService verificationService,
           final long userId) {
      final String rawToken = tokenGeneratorService.generateToken();
      final var userVerification = verificationService.save(userId, rawToken);
      verificationService.verify(userVerification.getUser().getEmail(), rawToken);
   }
}
