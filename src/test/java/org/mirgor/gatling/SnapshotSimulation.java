package org.mirgor.gatling;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

/**
 * High-load simulation for SnapshotService endpoints.
 *
 * Covers:
 *  - @Cacheable cache warm-up and hit rate (read-heavy scenario)
 *  - @CachePut / @CacheEvict correctness under concurrent writes (write-heavy scenario)
 *  - Realistic mixed CRUD workload
 *
 * Run via Maven:
 *   mvn gatling:test -DbaseUrl=http://localhost:8080 -DpeakUsers=100 -DrampSeconds=30 -DholdSeconds=60
 *
 * Or with defaults (50 peak users, 30 s ramp, 60 s hold, localhost:8080):
 *   mvn gatling:test
 */
public class SnapshotSimulation extends Simulation {

    // ── Configuration (override with -D system properties) ────────────────────

    private static final String BASE_URL =
            System.getProperty("baseUrl", "http://localhost:8080");

    private static final int PEAK_USERS =
            Integer.parseInt(System.getProperty("peakUsers", "500"));

    private static final int RAMP_SECONDS =
            Integer.parseInt(System.getProperty("rampSeconds", "10"));

    private static final int HOLD_SECONDS =
            Integer.parseInt(System.getProperty("holdSeconds", "60"));

    // Used to generate unique emails per virtual user across JVM instances
    private static final AtomicLong USER_COUNTER = new AtomicLong(System.currentTimeMillis());

    // ── HTTP protocol ─────────────────────────────────────────────────────────

    private final HttpProtocolBuilder httpProtocol = http
            .baseUrl(BASE_URL)
            .acceptHeader("application/json")
            .contentTypeHeader("application/json")
            .shareConnections();

    // ── Reusable chains ───────────────────────────────────────────────────────

    /**
     * Sign up a unique user and sign in, storing "jwt" in the session.
     */
    private final ChainBuilder authenticate = exec(session -> {
                long uid = USER_COUNTER.incrementAndGet();
                return session
                        .set("userEmail", "gatling_" + uid + "@loadtest.com")
                        .set("userPassword", "Gatling1!");
            })
            .exec(http("Sign Up")
                    .post("/api/auth/signup")
                    .body(StringBody(session ->
                            "{\"email\":\"" + session.getString("userEmail") + "\"," +
                            "\"password\":\"" + session.getString("userPassword") + "\"," +
                            "\"role\":\"USER\"}"
                    ))
                    .asJson()
                    // 201 = created, 409 = already exists (re-run), 400 = validation
                    .check(status().in(201, 400, 409))
            )
            .exec(http("Sign In")
                    .post("/api/auth/signin")
                    .body(StringBody(session ->
                            "{\"email\":\"" + session.getString("userEmail") + "\"," +
                            "\"password\":\"" + session.getString("userPassword") + "\"}"
                    ))
                    .asJson()
                    .check(status().is(200))
                    .check(jmesPath("token").saveAs("jwt"))
            );

    /**
     * Create a workspace and store its id as "workspaceId".
     */
    private final ChainBuilder createWorkspace = exec(http("Create Workspace")
            .post("/api/workspaces")
            .header("Authorization", session -> "Bearer " + session.getString("jwt"))
            // Email must be unique per user: (email, host) has a DB unique constraint.
            // Password must be a numeric string: @Min(6)/@Max(100) on String are parsed as BigDecimal by Hibernate Validator.
            .body(StringBody(session ->
                    "{\"email\":\"ws_" + session.getString("userEmail").hashCode() + "@thingsboard.io\"," +
                    "\"host\":\"http://thingsboard.local:8080\"," +
                    "\"password\":\"42\"," +
                    "\"currency\":\"USD\"," +
                    "\"pricingStrategy\":\"LATEST\"}"
            ))
            .asJson()
            .check(status().is(201))
            .check(jmesPath("id").ofLong().saveAs("workspaceId"))
    );

    /**
     * POST /api/snapshots — stores "snapshotId" in session.
     */
    private final ChainBuilder createSnapshot = exec(session ->
                    session.set("entityType", randomEntityType())
                           .set("snapshotCount", (long) (Math.random() * 1000) + 1)
            )
            .exec(http("POST Create Snapshot")
                    .post("/api/snapshots")
                    .header("Authorization", session -> "Bearer " + session.getString("jwt"))
                    .body(StringBody(session ->
                            "{\"workspaceId\":" + session.getLong("workspaceId") + "," +
                            "\"workspaceEntityType\":\"" + session.getString("entityType") + "\"," +
                            "\"count\":" + session.getLong("snapshotCount") + "}"
                    ))
                    .asJson()
                    .check(status().is(201))
                    .check(jmesPath("id").ofLong().saveAs("snapshotId"))
            );

    /**
     * GET /api/snapshots — hits the "workspace-snapshots" cache.
     */
    private final ChainBuilder getAllSnapshots = exec(http("GET All Snapshots")
            .get("/api/snapshots")
            .header("Authorization", session -> "Bearer " + session.getString("jwt"))
            .check(status().is(200))
    );

    /**
     * GET /api/snapshots?workspaceId=X — filtered list, also cached under "workspace-snapshots".
     */
    private final ChainBuilder getSnapshotsByWorkspace = exec(http("GET Snapshots by Workspace")
            .get(session -> "/api/snapshots?workspaceId=" + session.getLong("workspaceId"))
            .header("Authorization", session -> "Bearer " + session.getString("jwt"))
            .check(status().is(200))
    );

    /**
     * GET /api/snapshots/{id} — hits the "snapshots" cache (key = id).
     */
    private final ChainBuilder getSnapshotById = exec(http("GET Snapshot by Id")
            .get(session -> "/api/snapshots/" + session.getLong("snapshotId"))
            .header("Authorization", session -> "Bearer " + session.getString("jwt"))
            .check(status().in(200, 404))
    );

    /**
     * PUT /api/snapshots/{id} — triggers @CachePut + @CacheEvict(workspace-snapshots).
     */
    private final ChainBuilder updateSnapshot = exec(session ->
                    session.set("updatedCount", (long) (Math.random() * 2000) + 1)
            )
            .exec(http("PUT Update Snapshot")
                    .put(session -> "/api/snapshots/" + session.getLong("snapshotId"))
                    .header("Authorization", session -> "Bearer " + session.getString("jwt"))
                    .body(StringBody(session ->
                            "{\"workspaceId\":" + session.getLong("workspaceId") + "," +
                            "\"workspaceEntityType\":\"ASSET\"," +
                            "\"count\":" + session.getLong("updatedCount") + "}"
                    ))
                    .asJson()
                    .check(status().is(200))
            );

    /**
     * DELETE /api/snapshots/{id} — triggers @CacheEvict on both caches.
     */
    private final ChainBuilder deleteSnapshot = exec(http("DELETE Snapshot")
            .delete(session -> "/api/snapshots/" + session.getLong("snapshotId"))
            .header("Authorization", session -> "Bearer " + session.getString("jwt"))
            .check(status().is(204))
    );

    // ── Scenarios ─────────────────────────────────────────────────────────────

    /**
     * Read-Heavy: validates cache effectiveness.
     * Seeds one snapshot then hammers GET endpoints to stress @Cacheable.
     */
    private final ScenarioBuilder readHeavy = scenario("Read-Heavy – Cache Stress")
            .exec(authenticate)
            .exec(createWorkspace)
            .exec(createSnapshot)  // seed a record so GET-by-id returns 200
            .repeat(20).on(
                    exec(getAllSnapshots)
                            .pause(Duration.ofMillis(50), Duration.ofMillis(150))
                            .exec(getSnapshotsByWorkspace)
                            .pause(Duration.ofMillis(50), Duration.ofMillis(150))
                            .exec(getSnapshotById)
                            .pause(Duration.ofMillis(100), Duration.ofMillis(300))
            );

    /**
     * Write-Heavy: validates cache eviction correctness.
     * Rapid create → update → delete cycles to stress @CachePut / @CacheEvict.
     */
    private final ScenarioBuilder writeHeavy = scenario("Write-Heavy – Cache Eviction")
            .exec(authenticate)
            .exec(createWorkspace)
            .repeat(10).on(
                    exec(createSnapshot)
                            .pause(Duration.ofMillis(100), Duration.ofMillis(300))
                            .exec(updateSnapshot)
                            .pause(Duration.ofMillis(100), Duration.ofMillis(300))
                            .exec(deleteSnapshot)
                            .pause(Duration.ofMillis(200), Duration.ofMillis(500))
            );

    /**
     * Mixed: realistic concurrent workload — mostly reads, occasional writes.
     * Simulates typical production traffic distribution (~70 % reads, ~30 % writes).
     */
    private final ScenarioBuilder mixed = scenario("Mixed CRUD Workload")
            .exec(authenticate)
            .exec(createWorkspace)
            .exec(createSnapshot)
            .repeat(8).on(
                    exec(getAllSnapshots)
                            .pause(Duration.ofMillis(100), Duration.ofMillis(400))
                            .exec(getSnapshotsByWorkspace)
                            .pause(Duration.ofMillis(50),  Duration.ofMillis(200))
                            .exec(getSnapshotById)
                            .pause(Duration.ofMillis(50),  Duration.ofMillis(150))
                            .exec(createSnapshot)          // creates a new one (evicts workspace-snapshots cache)
                            .pause(Duration.ofMillis(100), Duration.ofMillis(300))
                            .exec(getAllSnapshots)          // should hit refreshed cache
                            .pause(Duration.ofMillis(50),  Duration.ofMillis(200))
                            .exec(updateSnapshot)
                            .pause(Duration.ofMillis(100), Duration.ofMillis(400))
                            .exec(getAllSnapshots)          // cache was evicted by update
                            .pause(Duration.ofMillis(50),  Duration.ofMillis(200))
                            .exec(deleteSnapshot)
                            .pause(Duration.ofMillis(200), Duration.ofMillis(600))
            );

    // ── Injection & assertions ─────────────────────────────────────────────────

    {
        setUp(
                readHeavy.injectOpen(
                        rampUsers(PEAK_USERS).during(Duration.ofSeconds(RAMP_SECONDS)),
                        constantUsersPerSec(PEAK_USERS / 3.0).during(Duration.ofSeconds(HOLD_SECONDS))
                ),
                writeHeavy.injectOpen(
                        nothingFor(Duration.ofSeconds(10)),
                        rampUsers(PEAK_USERS / 2).during(Duration.ofSeconds(RAMP_SECONDS)),
                        constantUsersPerSec(PEAK_USERS / 6.0).during(Duration.ofSeconds(HOLD_SECONDS))
                ),
                mixed.injectOpen(
                        nothingFor(Duration.ofSeconds(5)),
                        rampUsers(PEAK_USERS).during(Duration.ofSeconds(RAMP_SECONDS)),
                        constantUsersPerSec(PEAK_USERS / 3.0).during(Duration.ofSeconds(HOLD_SECONDS))
                )
        )
        .protocols(httpProtocol)
        .assertions(
                // p95 under 2 s, p99 under 5 s
                global().responseTime().percentile(95).lt(2000),
                global().responseTime().percentile(99).lt(5000),
                // at least 95 % of all requests succeed
                global().successfulRequests().percent().gt(95.0)
        );
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static String randomEntityType() {
        String[] types = {"DEVICE", "ASSET", "CUSTOMER"};
        return types[(int) (Math.random() * types.length)];
    }
}
