# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Purpose

This project charges ThingsBoard tenants for telemetry and memory utilization, creating invoices using Stripe. The broader goal is learning: Databases, Redis, Kafka, Testing, and large project workflows.

## Build & Run Commands

```bash
# Build (skip tests)
./mvnw clean package -DskipTests

# Run the application
./mvnw spring-boot:run

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=SecurityTest

# Run a single test method
./mvnw test -Dtest=SecurityTest#validJwtTokenShouldGrantAccess
```

## Required Environment Variables

The application will not start without these:

```
JWT_SECRET=<base64-encoded-secret>
JWT_EXPIRATION=<millis, e.g. 3600000>
```

PostgreSQL must be running at `localhost:5432` with database `tb_payment` (user/password: `postgres`/`postgres`). Schema is auto-managed by Hibernate (`ddl-auto=update`).

## Architecture

Spring Boot 4.0.2 application on Java 25. Uses standard Spring MVC (not reactive) for the REST API, but also imports `spring-boot-starter-webflux` solely to use `WebClient` for outbound HTTP calls.

### Layered Structure

```
Controller → Service → DaoService → Repository → DB
```

- **Controllers** (`org.mirgor.controller`): REST endpoints. Map DTOs in/out via mappers, delegate all logic to Services.
- **Services** (`org.mirgor.service`): Business logic. Enforce ownership/authorization using `SecurityUtil.getCurrentUserId()` and `SecurityUtil.getCurrentUserRole()`.
- **DAO Services** (`org.mirgor.service.dao`): Thin wrappers around JPA repositories that own `@Transactional` boundaries. Direct repository access from outside DAO services is avoided.
- **Repositories** (`org.mirgor.repository`): Spring Data JPA interfaces.
- **Mappers** (`org.mirgor.service.mapper`): Convert between entities and DTOs.

### Domain Model

- **User** — has a `Role` (USER or ADMIN).
- **Workspace** — belongs to a User; stores external system credentials (`host`, `email`, `password`) and a `SyncStatus` (PENDING / ACTIVE / INACTIVE).
- **Operation** — belongs to a Workspace; tracks `OperationalEntityType` and a count.
- **Price** — belongs to a Workspace; stores pricing info per `OperationalEntityType` and `Currency`.

### Authorization Pattern

- `/api/auth/signup` and `/api/auth/signin` are public; all other endpoints require a valid JWT (`Authorization: Bearer <token>`).
- Services enforce row-level ownership: users see only their own workspaces/operations/prices; ADMINs see everything.
- Admin-only endpoints use `@PreAuthorize("hasRole('ADMIN')")` on the controller method.
- `@EnableMethodSecurity` is active.

### Workspace Sync

`WorkspaceSynchronizationService` runs a scheduled job (default: every 60 seconds, configurable via `workspace.sync.rate_millis`) that pings each workspace by POSTing to `{workspace.host}/api/auth/login`. Success → `SyncStatus.ACTIVE`; failure → `SyncStatus.INACTIVE`. Ping callbacks are processed on a fixed thread pool (`executor.sync.threads_count`, default 32). Manual sync trigger is available via `POST /api/workspaces/sync` (ADMIN only).

### Security

JWT is issued on sign-in and validated by `JwtAuthFilter` on every request. `SecurityUser` is the `UserDetails` implementation, carrying the user's DB `id` alongside standard fields. `SecurityUtil` provides static helpers to read the authenticated user's ID and role from `SecurityContextHolder`.

### Tests

Integration tests in `src/test/java/org/mirgor/controller/SecurityTest.java` use `@SpringBootTest` + `MockMvc` + Testcontainers (PostgreSQL 15). Required properties (`datasource`, `jwt.secret`, `jwt.expiration`) are injected via `@DynamicPropertySource`. Docker must be running for tests to pass.
