# CLAUDE.md — FleetFinder Developer Reference

FleetFinder is a full-stack web application that helps players of the MMO *Star Citizen* find and join gameplay groups. This document covers architecture, conventions, and system design to help developers (and AI assistants) navigate the codebase.

---

## Table of Contents

1. [Tech Stack](#1-tech-stack)
2. [Security Approach](#2-security-approach)
3. [Database Architecture](#3-database-architecture)
4. [Services and Controller Structure](#4-services-and-controller-structure)
5. [WebSockets and STOMP Messaging](#5-websockets-and-stomp-messaging)
6. [Frontend Angular UI](#6-frontend-angular-ui)

---

## 1. Tech Stack

### Backend
- **Java 21**, **Spring Boot 4.0.1** (Tomcat 11)
- **Spring Data JPA + Hibernate** — MySQL 8.2, MySQL Connector 9.2.0
- **Spring Security** — OAuth2 Resource Server / JWT
- **Spring WebSocket** — STOMP protocol
- **Flyway 11.20.0** — 12 migration scripts (no V10)
- **EhCache 3.7.1** via JCache
- **ModelMapper 3.2.0** — DTO mapping
- **Lombok**, **dotenv-java**, **Commons-IO**
- **Build:** Maven 3.x

### Frontend
- **Angular 19.2.17** + **TypeScript 5.6.2**
- **Angular Material 19.2.17** + CDK
- **Bootstrap 5.3.3**
- **angular-oauth2-oidc 19.0.0**, **keycloak-angular 19.0.2**, **keycloak-js 26.2.0**
- **STOMP.js 7.2.1** + **SockJS 1.6.1** (WebSocket client)
- **Luxon 3.7.2** (date/time), **ng-select**

### Auth / Identity
- **Keycloak 26.0.5** — IAM, OIDC provider, realm: `oauthrealm`
- **JWT tokens** — validated by Spring as OAuth2 Resource Server

### DevOps
- **Docker + Docker Compose** — dev and prod configurations
- **Jenkins CI/CD** → AWS ECR → AWS EC2
- **NGINX** reverse proxy, **Certbot** SSL

### Testing
- **Backend:** JUnit 5, Mockito 5.14.2, MockMVC, TestContainers 1.21.4, AssertJ
- **Frontend:** Jasmine 5.4.0 + Karma 6.4.0

---

## 2. Security Approach

**Key file:** `backend/src/main/java/com/sc_fleetfinder/fleets/config/SecurityConfig.java`

### Authentication Flow

1. User authenticates with Keycloak via OIDC and receives a signed JWT.
2. JWT is sent as `Authorization: Bearer <token>` on every API request.
3. Spring Security validates the JWT via `jwk-set-uri` (Keycloak's JWK endpoint).
4. Custom `keycloakJwtAuthConverter` extracts `realm_access.roles` from JWT claims and prefixes each with `ROLE_` for Spring Security compatibility.
5. `JwtAuthenticationToken` principal is available in controllers via `@AuthenticationPrincipal Jwt jwt`.

### Roles

| Role | Description |
|------|-------------|
| `ROLE_user` | Standard authenticated users |
| `ROLE_mod` | Moderators with elevated permissions |

### Endpoint Access Rules

Defined in `SecurityConfig.java` in this order (first match wins):

```
/websocket/**                        → permitAll (JWT auth handled at STOMP layer)
/api/users/me                        → authenticated()
/api/users/**                        → authenticated()
/api/group-listings/create_listing   → authenticated()
/api/group-listings                  → permitAll
/api/modctrl/**                      → hasRole("mod")
anyRequest                           → permitAll
```

CSRF is disabled (JWT-based stateless auth).

### Controller Authorization Pattern

```java
String kcId = jwt.getSubject();
Users requestingUser = userRepository.findByKeycloakId(kcId)
    .orElseThrow(() -> new ResourceNotFoundException(...));
```

Cross-user operations throw `ActionNotAuthorizedException` (HTTP 401).

### Method-Level Security

```java
@PreAuthorize("isAuthenticated() and hasRole('user')")  // user-scoped operations
@PreAuthorize("isAuthenticated() and hasRole('mod')")   // moderation operations
```

### WebSocket JWT Auth

Three components handle WebSocket authentication:

| Class | Role |
|-------|------|
| `JwtQueryParamHandshakeInterceptor` | Extracts JWT from `?token=<jwt>` query param at HTTP upgrade |
| `JwtSubHandshakeHandler` | Sets `Principal` from JWT `sub` claim on the WebSocket session |
| `StompJwtChannelInterceptor` | Validates JWT on each inbound STOMP frame |

---

## 3. Database Architecture

**DBMS:** MySQL 8.2
**Schema:** `sc_fleetfinder`
**Migrations:** Flyway — `backend/src/main/resources/migration/`

### Migration History

| Version | File | Purpose |
|---------|------|---------|
| V1 | `V1__create_schema.sql` | Initial schema (users, reference data, group_listings) |
| V2 | `V2__insert_lookup_data.sql` | Reference data inserts (lookup tables) |
| V3 | `V3__add_bookmarks_table.sql` | Listing bookmarks |
| V4 | `V4__add_listing_archive_table.sql` | Listing archive |
| V5 | `V5__add_listing_report_tables.sql` | Moderation and reporting tables |
| V6 | `V6__add_hidden_listings.sql` | Hidden listings |
| V7 | `V7__add_listing_templates.sql` | Listing templates |
| V8 | `V8__add_chat_tables.sql` | Chat tables |
| V9 | `V9__add_notification_table.sql` | Notifications |
| V11 | `V11__add_notification_outbox.sql` | Notification outbox pattern |
| V12 | `V12__group_listing_add_lang_null_desc_lengthen_title.sql` | Schema tweaks (lang field, nullable desc, longer title) |

> Note: There is no V10 — the sequence jumps from V9 to V11.

### Core Entity Relationships

```
users (id_user, keycloak_id [UNIQUE], user_name [UNIQUE], email [UNIQUE], server_id, org, ...)
  └─ OneToMany → group_listing (ON DELETE CASCADE)
  └─ OneToOne  → user_moderation_record

group_listing (id_group, id_user FK, server_id, environment_id, experience_id,
               category_id, subcategory_id, pvp_status_id, system_id, planet_id, ...)
  └─ ManyToOne → users, server_region, game_environment, game_experience,
                  gameplay_category, gameplay_subcategory, legality,
                  group_status, pvp_status, play_style, planetary_system, planet_moon_system
  └─ vis_status ENUM: FRESH | RECENT | STALE | INACTIVE | EXPIRED | ARCHIVED

listing_archive, listing_bookmark, hidden_listing, listing_template
  └─ ManyToOne → users, group_listing

conversation, message, participant   (chat system)

notification, notification_outbox    (async notifications via outbox pattern)

moderation_issue (id_group FK, status ENUM: Pending | No Reports | Cleared | Actioned)
  └─ OneToMany → listing_report
listing_report (UNIQUE on id_group + id_reporter — prevents duplicate reports)
mod_listing_action (moderator audit log)
user_moderation_record (ban status, action count)
listing_report_basis (reference data for report categories)
```

### Reference / Lookup Tables

Read-heavy tables backed by EhCache (1-hour TTL):

- `gameplay_category`, `gameplay_subcategory`
- `server_region`, `game_environment`, `game_experience`
- `play_style`, `group_status`, `legality`, `pvp_status`
- `planetary_system`, `planet_moon_system`

Cache config: `backend/src/main/resources/ehcache.xml`
All 12 reference data types have dedicated caching services in `services/caching_services/`.

### JPA Configuration

- **`JpaTxConfig.java`** — transaction management, HikariCP connection pooling
- Hibernate batch fetch size: 20
- Dev profile: `spring.flyway.baseline-on-migrate=false`
- Prod profile: `spring.flyway.baseline-on-migrate=true`

---

## 4. Services and Controller Structure

**Package root:** `com.sc_fleetfinder.fleets`

### Controllers

| Controller | Base Path | Notes |
|-----------|-----------|-------|
| `GroupListingsController` | `/api/group-listings` | Search, CRUD for listings |
| `UserController` | `/api/users` | Profile, bookmarks, templates, hidden listings, reports |
| `ChatController` | `/api/chat` | Conversations, messages |
| `NotificationController` | `/api/notify` | Notification retrieval and delete |
| `ModerationController` | `/api/modctrl` | Mod-only; `hasRole("mod")` |
| `ListingReferenceDataControllers/*` | `/api/lookup/**` | Read-only reference data |
| `ChatWebSocketController` | STOMP `@MessageMapping` | Chat read receipts |
| `WebsocketNotificationController` | STOMP `@MessageMapping` | Notification read receipts |

**Common controller pattern:**

```java
@RestController
@RequestMapping("/api/...")
@PreAuthorize(...)
public class XController {
    @PostMapping("/action")
    public ResponseEntity<Dto> action(@AuthenticationPrincipal Jwt jwt, @RequestBody RequestDto dto) {
        String kcId = jwt.getSubject();
        Users user = userService.verifyUser(kcId);
        // delegate to service
    }
}
```

### Service Layer

Services live in `services/` with subdirectories by domain:

| Directory | Contents |
|-----------|---------|
| `CRUD_services/` | `UserService`, `GroupListingService`, `ChatService`, `NotificationService` |
| `chat_services/` | `ChatWsService` (WebSocket-specific chat ops) |
| `caching_services/` | One caching service per reference data type |
| `conversion_services/` | DTO↔Entity converters (chat, listings, moderation, reference data) |
| `listing_services/` | `ListingBookmarkService`, `ListingTemplateService` |
| `moderation_services/` | `ModerationService`, `ListingReportingService` |
| `referencedata_services/` | One service per lookup table |
| `archive_services/` | Listing archival logic |

**DTO mapping:** ModelMapper beans configured in `config/mappers/`.
Custom mappings per entity type (e.g., `GroupListingResponseDtoMapperConfig.java`, `ConversationMapperConfig.java`).

### Exception Types

| Exception | HTTP Status | Notes |
|-----------|------------|-------|
| `ResourceNotFoundException` | 404 | Resource not found |
| `ActionNotAuthorizedException` | 401 | Logs user ID, action, entity |

Global exception handlers live in `exceptions/handlers/`.

**Scheduled tasks:** `scheduledTasks/BackgroundCleanupService` — periodic data cleanup.

---

## 5. WebSockets and STOMP Messaging

**Config file:** `backend/src/main/java/com/sc_fleetfinder/fleets/config/WebSocketConfig.java`

**Endpoint:** `/websocket`
**Max message size:** 256 KB
**Allowed origins:** `app.ws.allowed-origins` property (env-specific, not hardcoded)

### Destination Prefixes

| Prefix | Direction | Purpose |
|--------|-----------|---------|
| `/app` | Client → Server | Handled by `@MessageMapping` controllers |
| `/topic` | Server → All | Broadcast to all subscribers |
| `/queue` | Server → User | Per-user queues |
| `/user` | Server → User | User-destination prefix (routes to `/user/{id}/queue/...`) |

### JWT Authentication at STOMP Layer

1. `JwtQueryParamHandshakeInterceptor` — reads `?token=<jwt>` at HTTP upgrade
2. `JwtSubHandshakeHandler` — sets `Principal` from JWT `sub` on the WebSocket session
3. `StompJwtChannelInterceptor` — validates JWT on each inbound STOMP frame

### Message Flow — Chat

| Direction | Destination | Payload |
|-----------|------------|---------|
| Client → Server | `/app/chat.read` | `ChatReadDto` (convId, lastReadMsgId) |
| Client → Server | `/app/chat.total_unread` | (empty) |
| Server → Client | `/user/queue/chat.unread` | `UserUnreadResponseDto` (totalUnread, Map<convId, count>) |

### Message Flow — Notifications

| Direction | Destination | Payload |
|-----------|------------|---------|
| Client → Server | `/app/system.notify/receive_read` | `ReceiveReadNotesDto` |
| Client → Server | `/app/system.notify/get_unread` | (empty) |
| Server → Client | `/user/queue/system.notify_count` | `NotificationUnreadCountDto` |
| Server → Client | `/user/queue/system.notify` | `NotificationViewModel` (pushed on new notification) |

**Angular WebSocket service:** `frontend/angular-fleets/src/app/services/.../WsGatewayService`
Uses `@stomp/stompjs` + `sockjs-client` to connect and subscribe.

---

## 6. Frontend Angular UI

**Root:** `frontend/angular-fleets/src/app/`

### Routing (`app-routing.module.ts`)

| Route | Component | Guard |
|-------|-----------|-------|
| `/` | `WelcomeScreenComponent` | — |
| `/login` | `LoginModalComponent` | — |
| `/group-listings` | `GroupListingsComponent` | — |
| `/create-listing` | `CreateListingComponent` | `AuthGuard` |
| `/update-listing` | `UpdateListingComponent` | `AuthGuard` |
| `/user-account` | `UserComponent` | `AuthGuard` |
| `/about` | `AboutComponent` | — |
| `/listing-success` | `ListingSuccessComponent` | `AuthGuard` |
| `/how-to` | `HowToComponent` | — |

### Component Organization (`components/`)

| Category | Components |
|----------|-----------|
| Layout | `NavBarComponent`, `FooterComponent` |
| Listings | `GroupListingsComponent`, `CreateListingComponent`, `UpdateListingComponent`, `GroupListingModalComponent` |
| Table/Feed views | `DesktopTableViewComponent`, `MobileFeedViewComponent` |
| Search/Filter | `SearchBarComponent`, `FilterDropdownsComponent`, `MobileFiltersPopupComponent` |
| Dropdowns (DropdownModule) | One per reference data type — `CategoryDropdownComponent`, `SubcategoryDropdownComponent`, `ServerDropdownComponent`, `SystemDropdownComponent`, `PlanetDropdownComponent`, etc. |
| Input fields (InputFieldModule) | `ListingTitleInputComponent`, `ListingDescriptionInputComponent`, `AvailableRolesInputComponent`, `EventScheduleDatepickerInputComponent`, etc. |
| Chat | `ChatShellComponent`, `ChatPanelComponent` |
| Moderation | `ModListingsTableComponent` |
| User | `UserComponent`, `UserProfileBookmarksComponent`, `UserAccountListingsTableComponent` |
| Dialogs/Modals | `ConfirmDeleteComponent`, `ConfirmReportComponent`, `ConfirmGenericComponent`, `LoginModalComponent`, `ModIssueDetailedComponent`, `DontShowMeAgainPopup` |

### Services (`services/`)

| Category | Services |
|----------|---------|
| API | `GroupListingFetchService`, `ChatApiService`, `UserApiService`, `NotificationApiService`, `BookmarkApiService`, `HiddenListingsApiService`, `ModApiService`, `LookupService`, `ListingTemplatesApiService`, `ListingReportApiService` |
| WebSocket | `WsGatewayService` |
| Facade/Store | `ChatHostService`, `ChatStoreService`, `NotificationService`, `UserService` |
| UI | `UiPrefsService`, `ListingViewInteractionsService`, `ListingOwnerActionsService`, `QuickAccessMenuService`, `TemplatesModalService` |
| Forms | `ListingFormService`, `CustomValidators`, `FilterService` |
| Auth | `AuthService` |

### Models (`models/`)

TypeScript interfaces mirroring backend DTOs:

| Domain | Interfaces |
|--------|-----------|
| Listings | `GroupListingViewModel`, `CreateListingRequest`, `UpdateListingRequest`, `ListingFilterRequest` |
| Chat | `MessageViewModel`, `ConversationViewModel`, `SendMessageRequest` |
| User | `PrivateUser`, `PublicUser` |
| Moderation | `ModIssueViewModel`, `ModListingActionViewModel` |
| Notifications | `NotificationViewModel` |
| Other | Bookmarks, Templates, Reports — corresponding request/response models |

### Auth

- OIDC configuration points to Keycloak realm `oauthrealm`
- JWT attached to all HTTP requests via Angular HTTP interceptor
- JWT also passed as query param on WebSocket connection (`?token=<jwt>`)
- `AuthGuard` protects authenticated routes
- Libraries: `angular-oauth2-oidc` + `keycloak-angular`
