# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

FleetFinder is a full-stack web app for *Star Citizen* players to find and join gameplay groups. Backend: Java 21 / Spring Boot 4 / MySQL 8.2. Frontend: Angular 19 / TypeScript. Auth: Keycloak 26 (OIDC/JWT).

---

## Commands

### Backend (Maven — run from `backend/`)

Use the Maven wrapper (`./mvnw`), not `mvn` directly — Maven is not on the system PATH.

```bash
./mvnw clean package          # Build JAR
./mvnw clean package -DskipTests
./mvnw test                   # All tests
./mvnw test -Dtest=ClassName  # Single test class
./mvnw test -Dtest=ClassName#methodName  # Single test method
```

Tests use JUnit 5, Mockito, MockMVC, TestContainers (requires Docker), AssertJ.

**Test structure:**
```
src/test/java/com/sc_fleetfinder/fleets/
  unit_tests/
    controllers/         # @WebMvcTest + @Import(SecurityConfig.class), @MockitoBean JwtDecoder
    services/
      archive_services/
      CRUD_services/
      listing_services/  # ListingTemplateServiceImplTest
      mod_services/      # ModerationServiceImplTest
      reporting_services/ # ListingReportingServiceImplTest
      caching_services/
      conversion_services/
    scheduledTasks/
  integration_tests/     # @SpringBootTest + AbstractIntegrationTestDB (TestContainers mysql:8.2)
```

**Key test patterns:**
- Controller tests: `@WebMvcTest`, `@Import(SecurityConfig.class)`, JWT via `.with(jwt().authorities(...))`
- Integration tests: extend `AbstractIntegrationTestDB`, annotate `@Transactional` (auto-rollback), inject `JdbcTemplate` for raw SQL setup
- Services with `@Autowired` field injection alongside constructor injection: after `@InjectMocks`, manually inject `@Autowired` fields via `ReflectionTestUtils.setField()` in `@BeforeEach`
- Over-stubbed shared mock helpers: use `@MockitoSettings(strictness = Strictness.LENIENT)` on the class

**Integration test gotchas:**
- `SecurityConfig` is `@Profile("!test")` — not loaded in integration tests. Default Spring Security CSRF is **enabled**. Unauthenticated POST without `.with(csrf())` returns 403 (CSRF fail), not 401. Always add `.with(csrf())` to unauthenticated POST tests.
- Spring Boot 4 `Page<>` JSON: `totalElements` is at `$.page.totalElements`, not `$.totalElements`.
- `jdbcTemplate.queryForObject(..., Timestamp.class).toInstant()` applies the JVM timezone and gives wrong UTC values. For timestamp comparisons use `UNIX_TIMESTAMP(col)` in SQL and compare to `instant.getEpochSecond()`.
- `ZoneId.of()` requires IANA names (`"America/Los_Angeles"`, `"UTC"`). Windows-style names (`"Pacific Standard Time"`) throw at runtime.

### Frontend (npm — run from `frontend/angular-fleets/`)

```bash
npm start          # Dev server at localhost:4200 (proxies /api → localhost:8080)
npm run build      # Production build → dist/angular-fleets/
npm test           # Jasmine + Karma unit tests
npm run watch      # Build in watch mode
```

Proxy config: `frontend/angular-fleets/proxy.conf.json` — routes `/api/**` to `http://localhost:8080`.

### Dev Environment (Docker)

```bash
docker-compose -f backend/docker-compose.dev.yml up -d   # Start MySQL + Keycloak
```

---

## Architecture Overview

### Security Flow

1. User authenticates with Keycloak (OIDC), receives a signed JWT.
2. JWT sent as `Authorization: Bearer <token>` on all API requests.
3. Spring validates JWT via `jwk-set-uri`. Custom `keycloakJwtAuthConverter` extracts `realm_access.roles` and prefixes with `ROLE_`.
4. Controllers use `@AuthenticationPrincipal Jwt jwt` → `jwt.getSubject()` to identify the user.

Endpoint access order in `SecurityConfig.java` (first match wins):
```
/websocket/**                      → permitAll
/api/users/**                      → authenticated()
/api/group-listings/create_listing → authenticated()
/api/group-listings                → permitAll
/api/modctrl/**                    → hasRole("mod")
anyRequest                         → permitAll
```

### WebSocket / STOMP Auth

Three components handle WS auth: `JwtQueryParamHandshakeInterceptor` (reads `?token=<jwt>` at HTTP upgrade) → `JwtSubHandshakeHandler` (sets `Principal` from JWT `sub`) → `StompJwtChannelInterceptor` (validates JWT on each STOMP frame).

STOMP destination prefixes: `/app` (client→server), `/topic` (broadcast), `/queue` and `/user` (per-user).

### Backend Package Structure

Root: `com.sc_fleetfinder.fleets`

```
config/              # SecurityConfig, WebSocketConfig, JpaTxConfig, EhCache, ModelMapper beans
controllers/         # REST + STOMP @MessageMapping controllers
services/
  CRUD_services/     # UserService, GroupListingService, ChatService, NotificationService
  caching_services/  # One per reference data type (EhCache, 1-hour TTL)
  conversion_services/ # DTO↔Entity converters
  listing_services/  # BookmarkService, TemplateService
  moderation_services/
  archive_services/
  referencedata_services/
entities/            # JPA entities
DAO/                 # Spring Data JPA repositories
DTO/                 # requestDTOs/ and responseDTOs/
exceptions/          # ResourceNotFoundException (404), ActionNotAuthorizedException (401) + handlers
scheduledTasks/      # BackgroundCleanupService
utils/               # LanguageOptions enum (28 spoken language constants)
```

### Database

Schema: `sc_fleetfinder`. Migrations: Flyway V1–V9, V11–V13 (no V10) in `backend/src/main/resources/migration/`. V13 makes `language_code` NOT NULL on `group_listing`, `listing_template`, and `listing_archive`.

Key relationships:
- `users` → OneToMany `group_listing` (CASCADE DELETE)
- `group_listing` → ManyToOne to all reference/lookup tables
- `group_listing.vis_status` ENUM: `FRESH | RECENT | STALE | INACTIVE | EXPIRED | ARCHIVED`
- Lookup/reference tables (category, server, environment, experience, etc.) are read-heavy and cached via EhCache (`ehcache.xml`).

Spring profiles:
- `dev`: `spring.flyway.baseline-on-migrate=false`
- `prod`: `spring.flyway.baseline-on-migrate=true`

### Frontend Structure

Root: `frontend/angular-fleets/src/app/`

```
components/   # UI — organized by feature (listings, chat, moderation, user, dropdowns, input fields, dialogs)
services/     # API clients, WsGatewayService, facade/store services, form services
models/
  reference-data/reference-data.models.ts  # Typed interfaces for all 11 lookup/reference data types
                                           # (ServerRegion, GameEnvironment, GameExperience,
                                           #  GameplayCategory, GameplaySubcategory, PlayStyle,
                                           #  GroupStatus, Legality, PvpStatus,
                                           #  PlanetarySystem, PlanetMoonSystem)
  group-listing/  # GroupListingViewModel, CreateListingRequest, UpdateListingRequest
  language-options.ts  # LANGUAGE_OPTIONS const array + LanguageCode union type (as const)
  # ...other domain models (chat, user, moderation, notifications, templates, etc.)
```

**Reference data typing conventions:**
- `LookupService` returns `Observable<InterfaceType[]>` — never `any[]`
- Dropdown component data arrays are typed as `InterfaceType[]` — never inline object types
- Form controls for reference data store the **ID only** (`FormControl<number | null>`), not the full object

Auth: `angular-oauth2-oidc` + `keycloak-angular` → JWT attached to all HTTP requests via interceptor. Same JWT passed as query param on WebSocket connection.

Angular Material + Bootstrap 5.3.3 for UI. Luxon for date/time. `@stomp/stompjs` + `sockjs-client` for WebSocket.
