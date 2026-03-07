# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

FleetFinder is a full-stack web app for *Star Citizen* players to find and join gameplay groups. Backend: Java 21 / Spring Boot 4 / MySQL 8.2. Frontend: Angular 19 / TypeScript. Auth: Keycloak 26 (OIDC/JWT).

---

## Commands

### Backend (Maven — run from `backend/`)

```bash
mvn clean package          # Build JAR
mvn clean package -DskipTests
mvn test                   # All tests
mvn test -Dtest=ClassName  # Single test class
mvn test -Dtest=ClassName#methodName  # Single test method
```

Tests use JUnit 5, Mockito, MockMVC, TestContainers (requires Docker), AssertJ.

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
```

### Database

Schema: `sc_fleetfinder`. Migrations: Flyway V1–V9, V11–V12 (no V10) in `backend/src/main/resources/migration/`.

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
models/       # TypeScript interfaces mirroring backend DTOs
```

Auth: `angular-oauth2-oidc` + `keycloak-angular` → JWT attached to all HTTP requests via interceptor. Same JWT passed as query param on WebSocket connection.

Angular Material + Bootstrap 5.3.3 for UI. Luxon for date/time. `@stomp/stompjs` + `sockjs-client` for WebSocket.
