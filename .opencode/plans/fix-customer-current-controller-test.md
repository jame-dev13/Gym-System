# Fix CustomerUserControllerTest for the "current customer in session" resources

## Goal
Make `src/test/java/com/jame/dev/gymApp/customer/controller/CustomerUserControllerTest.java`
pass against the reworked `CustomerCurrentController`, whose 4 endpoints now operate on the
authenticated (current) customer via `Authentication` and `CustomerCurrentRequest`.

Scope decisions (confirmed with user):
- Fix the controller test only — do NOT add service-level tests for the 3 "current" use-case services.
- Leave `CustomerCurrentController` untouched (do NOT fix the `/currrent` typo nor the broken
  `@PreAuthorize("...isOwner(#id,...)")` on PUT); the test will hit the actual mapped path `/currrent`.

## Current mismatch (why the test fails)
- The controller depends on `CreateCurrentCustomerUseCase`, `GetCurrentCustomerUseCase`,
  `UpdateCurrentCustomerUseCase`, `DeleteCurrentCustomerUseCase`; the test mocks the OLD ones
  (`CreateCustomerUseCase`, `UpdateCustomerUseCase`, `SoftDeleteCustomerByIdUseCase`) → Spring
  test context cannot build (missing beans).
- Endpoints changed: POST `/`, PUT `/{id}`, DELETE `/{id}` → now POST/GET/DELETE `/current`, PUT `/currrent`.
- Payload changed from `{userEmail, contact}` to a single `{phoneContact}` field
  (`CustomerCurrentRequest`, `@JsonProperty("phoneContact") @NotEmpty`).
- MockMvc (filters disabled) passes `Authentication == null`; Mockito `any(Authentication.class)`
  does NOT match null → stubbing/verification must use `any()` for the auth argument.

## Changes

### 1. `src/test/java/config/TestDataSource.java`
Add a new (additive, shared-safe) constant:
```java
String PHONE_CONTACT_VALIDATIONS_ERRORS = """
         PHONE,   ERROR_CODE
         EMPTY,   VALIDATION_OPERATION
         NULL,    VALIDATION_OPERATION
         MISSING, VALIDATION_OPERATION
         """;
```

### 2. `src/test/java/com/jame/dev/gymApp/customer/controller/CustomerUserControllerTest.java`
Full rewrite of imports, mocks, payloads, URIs and edge cases. Outline:

- Imports: replace `CustomerRequest`, `CreateCustomerUseCase`, `UpdateCustomerUseCase`,
  `SoftDeleteCustomerByIdUseCase`, `CustomerNotFoundException` with
  `CustomerCurrentRequest`, `CreateCurrentCustomerUseCase`, `UpdateCurrentCustomerUseCase`,
  `DeleteCurrentCustomerUseCase`, `com.jame.dev.gymApp.domain.exception.NotFoundException`,
  `com.jame.dev.gymApp.features.auth.domain.exception.AuthenticationNullException`.
  Drop `anyLong`, `MockMvcRequestBuilders` (non-static), `Authentication` (unused).
  `doThrow` comes from `Mockito.*`.
- `@MockitoBean` fields:
  - `CreateCurrentCustomerUseCase create`
  - `GetCurrentCustomerUseCase currentCustomerUseCase` (kept)
  - `UpdateCurrentCustomerUseCase update`
  - `DeleteCurrentCustomerUseCase deleteCurrent`
- Payloads use `phoneContact` only.
- Station endpoints: `URI_TEMPLATE + "/current"` for GET/POST/DELETE; `URI_TEMPLATE + "/currrent"` for PUT.

Nested suites and edge cases (status code per `GlobalExceptionHandler`):

**GET `/current`**
- 200 OK — `getCurrent(any())` returns response; assert id + `verifyNoMoreInteractions`.
- 404 NOT_FOUND_OPERATION — service throws `NotFoundException`.
- 401 AUTHENTICATION_OPERATION — service throws `AuthenticationNullException`.

**POST `/current`**
- 201 Created — `createCurrent(any(), any(CustomerCurrentRequest.class))` returns response.
- 409 SAVE_OPERATION — `createCurrent` throws `AlreadyExistsException`.
- 409 VALIDATION_OPERATION — `createCurrent` throws `NoActiveException`.
- 401 AUTHENTICATION_OPERATION — `createCurrent` throws `AuthenticationNullException`.
- 400 VALIDATION_OPERATION — payload via `PHONE_CONTACT_VALIDATIONS_ERRORS`
  (EMPTY → `""`, NULL → JSON `null`, MISSING → `{}`) → `verifyNoInteractions(create)`.
- 400 VALIDATION_OPERATION — body format errors via `BODY_FORMAT_ERRORS` → `verifyNoInteractions(create)`.

**PUT `/currrent`**
- 200 OK — `updateCurrent(any(), any(CustomerCurrentRequest.class))` returns response.
- 404 NOT_FOUND_OPERATION — service throws `NotFoundException`.
- 401 AUTHENTICATION_OPERATION — service throws `AuthenticationNullException`.
- 400 VALIDATION_OPERATION — payload validations (`PHONE_CONTACT_VALIDATIONS_ERRORS`) → `verifyNoInteractions(update)`.
- 400 VALIDATION_OPERATION — body format (`BODY_FORMAT_ERRORS`) → `verifyNoInteractions(update)`.

**DELETE `/current`**
- 204 No Content — default no-op mock → `verify(deleteCurrent, times(1)).deleteCurrent(any())`.
- 401 AUTHENTICATION_OPERATION — `doThrow(AuthenticationNullException.class).when(deleteCurrent).deleteCurrent(any())`.

Validation test payload construction (handles CSV specials):
```java
String payload;
if (phone == null) payload = """ { "phoneContact": null } """;
else if ("MISSING".equals(phone)) payload = "{ }";
else payload = """ { "phoneContact": "%s" } """.formatted(phone);
```

## Verification
- `./mvnw test` (or targeted):
  `./mvnw test -Dtest=CustomerUserControllerTest`
- If the full suite is required afterward:
  `./mvnw test`

## Non-goals
- No service tests for the 3 "current" use-case services.
- No changes to `CustomerCurrentController` (keeps `/currrent` mapping and current `@PreAuthorize`);
  no fix for collateral functionality found.