# Gym App — Agent Guide

## Stack

Spring Boot 4.0.3, Java 21, Maven wrapper (`./mvnw`). Hexagonal/DDD with 7 feature modules: audit, auth, customer, metrics, notification, subscription, user. No frontend (external SPA at `http://localhost:5173`).

## Commands

| Action | Command |
|---|---|
| Build & run (dev) | `make dev-build` |
| Build & run (prod) | `make prod-build` |
| Up (dev) | `make dev-up` |
| Down (dev) | `make dev-down` |
| Package (skip tests) | `./mvnw clean package -Dmaven.test.skip=true` |
| Run tests | `./mvnw test` |

Only Docker Compose via `make` — no local Maven dev server exposed.

## Databases

- **PostgreSQL** (primary JPA), **MongoDB** (audit log), **Redis** (caching, tokens, rate-limiting).
- `MongoAutoConfiguration` excluded in `GymAppApplication` — MongoDB manually configured in `MongoConfig`.
- Redis uses 5 logical databases via Jedis `StringRedisTemplate` beans: DB 0 (cache), 1 (tokens/blacklist), 2 (rate-limiting), 3 (blocking), 4 (notifications).

## Profiles

- **`dev`** (default). `ddl-auto=create`. Seeds admin/user/customer/subscription data via `InitConfig` (`CommandLineRunner` ordered by `@Priority`).
- **`prod`**. `ddl-auto=update`. Secrets from `configtree:/run/secrets/`. Redis SSL enabled.

## Configuration

- `application.properties` imports `optional:file:.env[.properties]`
- `application-dev.properties` imports `optional:file:.env.dev[.properties]`
- `application-prod.properties` imports `configtree:/run/secrets/` + `optional:file:.env.prod[.properties]`
- `.env*` and `secrets/` are gitignored.

## Testing

- JUnit 5 + Mockito.
- **Service tests**: `@ExtendWith(MockitoExtension.class)`, `@Mock` / `@InjectMocks`.
- **Controller tests**: `@WebMvcTest(controllers = ..., excludeFilters = @ComponentScan.Filter(classes = CustomAuthorizationFilter.class, type = ASSIGNABLE_TYPE))` + `@AutoConfigureMockMvc(addFilters = false)`. Import `{GlobalExceptionHandler.class, TestConfig.class, TestValidationConfig.class}`.
- Test support classes in `src/test/java/config/` and `src/test/java/data/`.
- No integration tests requiring external services — repos are mocked.

## Package structure

```
com.jame.dev.gymApp
├── application/          shared contracts, DTOs, services
├── domain/               BaseEntity, generic exceptions, CustomJpaRepository
├── features/
│   ├── audit/            MongoDB audit logging via AOP
│   ├── auth/             JWT, OAuth2, rate-limiting, sessions, verification
│   ├── customer/         CRUD customer
│   ├── metrics/          dashboard metrics (earnings, subscriptions)
│   ├── notification/     email sending
│   ├── subscription/     membership/pricing/subscriptions (core domain)
│   └── user/             user & role management
├── infrastructure/       config, cache, web, annotations, sort, validation
└── presentation/         GlobalExceptionHandler, ApiErrorResponse
```

Each feature module: `api/` (controllers, request/response DTOs) → `application/` (ports, use cases, services) → `domain/` (entities, repos, events) → `infrastructure/` (adapters, persistence, security).

## Constraints

- Package name is `com.jame.dev.gymApp` (not `gym-app`).
- No Liquibase/Flyway — schema managed solely by Hibernate `ddl-auto`.
- No static resources or templates.
- Custom validators (`@EmailValid`, `@Minimum`, `@PositiveNum`, `@SortPropertyValid`) drive validation.
- `@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)` in `AppConfig`.
- Virtual threads enabled (`spring.threads.virtual.enabled=true`).
- `@EnableMethodSecurity` — method-level security annotations in use.
