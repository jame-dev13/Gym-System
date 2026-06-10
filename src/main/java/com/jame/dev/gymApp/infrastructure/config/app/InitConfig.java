package com.jame.dev.gymApp.infrastructure.config.app;

import com.jame.dev.gymApp.application.contract.TokenGeneratorService;
import com.jame.dev.gymApp.features.auth.application.contract.verification.VerificationService;
import com.jame.dev.gymApp.features.auth.application.model.AuthProvider;
import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.customer.infrastructure.persistence.CustomerRepository;
import com.jame.dev.gymApp.features.subscription.application.contract.MembershipService;
import com.jame.dev.gymApp.features.subscription.application.contract.PricingService;
import com.jame.dev.gymApp.features.subscription.domain.model.*;
import com.jame.dev.gymApp.features.subscription.infrastructure.persistence.SubscriptionRepository;
import com.jame.dev.gymApp.features.user.api.request.UserRequest;
import com.jame.dev.gymApp.features.user.application.contract.UserFactory;
import com.jame.dev.gymApp.features.user.domain.model.Role;
import com.jame.dev.gymApp.features.user.domain.model.RoleEntity;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import com.jame.dev.gymApp.features.user.infrastructure.persistence.RoleRepository;
import com.jame.dev.gymApp.features.user.infrastructure.persistence.UserRepository;
import jakarta.annotation.Priority;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.IntStream;

import static com.jame.dev.gymApp.features.subscription.domain.model.Membership.*;

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
   private final List<String> ORDER = List.of("biweekly", "monthly", "quarterly", "annual");
   private final Map<String, BigDecimal> prices = Map.ofEntries(
      Map.entry("biweekly", BigDecimal.valueOf(150.00d)),
      Map.entry("monthly", BigDecimal.valueOf(300.00d)),
      Map.entry("quarterly", BigDecimal.valueOf(900.00d)),
      Map.entry("annual", BigDecimal.valueOf(3600.00d))
   );

   private final Map<Membership, PricingEntity> pricingMap = new HashMap<>();

   @Bean
   @Priority(0)
   public CommandLineRunner runnerRoles(final RoleRepository roleRepository) {
      return args ->
         roleRepository.saveAll(Set.of(
            new RoleEntity(1, Role.ADMIN),
            new RoleEntity(2, Role.USER)
         ));
   }

   @Bean
   @Priority(1)
   public CommandLineRunner runnerInitUsersAndCustomers(final UserRepository userRepository,
                                                        final UserFactory userFactory,
                                                        final CustomerRepository customerRepository,
                                                        final TokenGeneratorService tokenService,
                                                        final VerificationService verificationService) {
      return args -> {
         final var admin = UserRequest.builder()
            .name("admin")
            .email(emailAdmin)
            .password(passwordAdmin)
            .roles(Set.of(Role.ADMIN))
            .authProvider(AuthProvider.LOCAL)
            .build();
         final var user = UserRequest.builder()
            .name("user")
            .email(emailUser)
            .password(passwordUser)
            .roles(Set.of(Role.USER))
            .authProvider(AuthProvider.LOCAL)
            .build();
         final var adminSaved = userRepository.save(userFactory.createFromInput(admin));
         saveAndVerifyUser(tokenService, verificationService, adminSaved);

         final var userSaved = userRepository.save(userFactory.createFromInput(user));
         saveAndVerifyUser(tokenService, verificationService, userSaved);

         final CustomerEntity customerRequest = new CustomerEntity(userSaved, "1112223334");
         customerRepository.save(customerRequest);

         log.info("Runner InitUsersAndCustomers end execution.");
      };
   }

   @Bean
   @Priority(2)
   public CommandLineRunner runnerMembershipsAndPrices(final MembershipService membershipService, final PricingService pricingService) {
      return args -> {

         final Map<String, MemberShipEntity> memberships = new LinkedHashMap<>();
         ORDER.forEach(name -> {
            final Membership type = Membership.valueOf(name.toUpperCase());
            final MemberShipEntity entity = membershipService.save(new MemberShipEntity(null, type));
            memberships.put(name, entity);
         });

         ORDER.forEach(name -> {
            final MemberShipEntity membership = memberships.get(name);
            final BigDecimal price = prices.get(name);

            final var pricingEntity = pricingService.save(new PricingEntity(null, membership, price));
            pricingMap.putIfAbsent(pricingEntity.getMemberShipEntity().getMembership(), pricingEntity);
         });
         log.info("Runner MembershipAndPrices end execution.");
      };
   }

   @Bean
   @Priority(3)
   public CommandLineRunner runnerCreationOfUsersCustomersAndSubscriptions(
      final UserRepository userRepository,
      final UserFactory userFactory,
      final TokenGeneratorService tokenGeneratorService,
      final VerificationService verificationService,
      final CustomerRepository customerRepository,
      final SubscriptionRepository subscriptionRepository
   ) {
      return args -> {
         log.info("Runner CreationOfUsersCustomersAndSubscriptions start execution.");
         IntStream.range(0, 20)
            .forEach(i -> {
               //Users
               final var user = createUser(i + 1);
               final var userEntity = userRepository.save(userFactory.createFromInput(user));
               saveAndVerifyUser(tokenGeneratorService, verificationService, userEntity);
               //Customers
               final var customer = createCustomer(userEntity, i + 1);
               final var customerEntity = customerRepository.save(customer);
               //Subscriptions
               final var subscription = createSubscription(customerEntity);
               subscriptionRepository.save(subscription);
            });
         log.info("Runner CreationOfUsersCustomersAndSubscriptions end execution.");
      };
   }

   private UserRequest createUser(final int i) {
      return UserRequest.builder()
         .name("user" + i)
         .email("user" + i + "@mail.com")
         .password("password" + i)
         .authProvider(AuthProvider.LOCAL)
         .roles(Set.of(Role.USER))
         .build();
   }

   private CustomerEntity createCustomer(final UserEntity user, final int i) {
      return new CustomerEntity(user, "8280101" + i);
   }

   private SubscriptionEntity createSubscription(final CustomerEntity customer) {
      final Membership[] memberships = {BIWEEKLY, MONTHLY, QUARTERLY, ANNUAL};
      final Period[] periods = {Period.BIWEEKLY, Period.MONTHLY, Period.QUARTERLY, Period.ANNUAL};
      final int randomIdx = random.nextInt(0, memberships.length);
      return SubscriptionEntity.builder()
         .customer(customer)
         .pricing(pricingMap.get(memberships[randomIdx]))
         .subscriptionPeriods(List.of(new PeriodEntity(periods[randomIdx], LocalDate.now())))
         .finished(random.nextBoolean())
         .build();
   }

   private void saveAndVerifyUser(
      final TokenGeneratorService tokenGeneratorService,
      final VerificationService verificationService,
      final UserEntity user) {
      final String rawToken = tokenGeneratorService.generateToken();
      final var userVerification = verificationService.save(user, rawToken);
      verificationService.verify(userVerification, rawToken);
   }
}
