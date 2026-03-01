# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Purpose

This project charges ThingsBoard tenants for telemetry and memory utilization, creating invoices using Stripe. The broader goal is learning: Databases, Redis, Kafka, Testing, and large project workflows.

## Build & Run Commands

```bash
# Build (skip tests)
mvn clean package -DskipTests

# Run the application
mvn spring-boot:run

# Run all tests
mvn test

# Run a single test class
mvn test -Dtest=SecurityTest

# Run a single test method
mvn test -Dtest=SecurityTest#validJwtTokenShouldGrantAccess
```

## Required Environment Variables

The application will not start without these:

```
JWT_SECRET=<base64-encoded-secret>
JWT_EXPIRATION=<millis, e.g. 3600000>
```

PostgreSQL must be running at `localhost:5432` with database `tb_payment` (userEntity/password: `postgres`/`postgres`). Schema is auto-managed by Hibernate (`ddl-auto=update`).

## Architecture

Spring Boot 4.0.2 application on Java 25. Uses standard Spring MVC (not reactive) for the REST API, but also imports `spring-boot-starter-webflux` solely to use `WebClient` for outbound HTTP calls.

### Layered Structure

```
Controller (DTO) → Service (DTO) → DaoService (maps DTO↔Entity internally) → Repository (Entity) → DB
```

- **Controllers** (`org.mirgor.controller`): REST endpoints. Work exclusively with DTOs; no mapper injection. Delegate all logic to Services.
- **Services** (`org.mirgor.service`): Business logic. Work exclusively with DTOs. Enforce ownership/authorization using `SecurityUtil.getCurrentUserId()` and `SecurityUtil.getCurrentUserRole()`.
- **DAO Services** (`org.mirgor.service.dao`): The sole entity↔DTO translation boundary. Own `@Transactional` boundaries, inject mappers, and return DTOs. Direct repository access from outside DAO services is avoided.
- **Repositories** (`org.mirgor.repository`): Spring Data JPA interfaces.
- **Mappers** (`org.mirgor.service.mapper`): Injected only by DAO services. `WorkspaceMapper` and `UserMapper` implement `EntityMapper<E, D>` (bidirectional). `SnapshotMapper` and `PriceMapper` are unidirectional (`toDto` only); `fromDto` logic is handled inline in their respective DAO services.

### Domain Model

- **User** — has a `Role` (USER or ADMIN).
- **Workspace** — belongs to a User; stores external system credentials (`host`, `email`, `password`) and a `SyncStatus` (PENDING / ACTIVE / INACTIVE).
- **Snapshot** (formerly Operation) — belongs to a Workspace; records a point-in-time count of a `SnapshotEntityType`. Entity class: `SnapshotEntity` (table: `snapshot`). DTO class: `Snapshot`.
- **Price** — belongs to a Workspace; stores pricing info per `SnapshotEntityType` and `Currency`.

### Authorization Pattern

- `/api/auth/signup` and `/api/auth/signin` are public; all other endpoints require a valid JWT (`Authorization: Bearer <token>`).
- Services enforce row-level ownership: users see only their own workspaces/snapshots/prices; ADMINs see everything.
- Admin-only endpoints use `@PreAuthorize("hasRole('ADMIN')")` on the controller method.
- `@EnableMethodSecurity` is active.

### Workspace Sync

`WorkspaceSynchronizationService` runs two scheduled jobs:
- **Ping sync** (default: every 60 s, `workspace.sync.rate_millis`): POSTs to `{workspace.host}/api/auth/login` for each workspace. Success → `SyncStatus.ACTIVE`; failure → `SyncStatus.INACTIVE`. Manual trigger: `POST /api/workspaces/sync` (ADMIN only).
- **Usage snapshot** (default: every 3600 s, `workspace.sync.usage_snapshot`): logs in to each workspace, counts entities per `SnapshotEntityType`, and persists a `Snapshot` record. Trigger: `POST /api/workspaces/snapshot` (ADMIN only) — returns `List<Snapshot>`.

Both jobs run on a fixed thread pool (`dbTaskExecutor`). The service works exclusively with `Workspace` and `Snapshot` DTOs; no entities cross its boundary.

### Security

JWT is issued on sign-in and validated by `JwtAuthFilter` on every request. `SecurityUser` is the `UserDetails` implementation, carrying the user's DB `id` alongside standard fields. `SecurityUtil` provides static helpers to read the authenticated user's ID and role from `SecurityContextHolder`.

### Tests

Integration tests in `src/test/java/org/mirgor/controller/SecurityTest.java` use `@SpringBootTest` + `MockMvc` + Testcontainers (PostgreSQL 15). Required properties (`datasource`, `jwt.secret`, `jwt.expiration`) are injected via `@DynamicPropertySource`. Docker must be running for tests to pass.
