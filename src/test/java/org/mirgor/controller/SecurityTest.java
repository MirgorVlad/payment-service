package org.mirgor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mirgor.common.constant.Role;
import org.mirgor.common.auth.AuthRequest;
import org.mirgor.common.auth.AuthResponse;
import org.mirgor.common.entity.User;
import org.mirgor.security.principal.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("jwt.secret", () -> "dGVzdHNlY3JldGtleXRoYXRpc2xvbmdlbm91Z2hmb3JoczI1NnNpZ25pbmc=");
        registry.add("jwt.expiration", () -> "3600000");
    }

    private String signUpAndGetToken(String email, String password, Role role) throws Exception {
        User user = User.builder()
                .email(email)
                .password(password)
                .role(role)
                .build();

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated());

        AuthRequest authRequest = new AuthRequest(email, password);
        MvcResult result = mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponse authResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(), AuthResponse.class);
        return authResponse.token();
    }

    // ==================== AUTH ENDPOINTS (PUBLIC) ====================
    @Nested
    @DisplayName("Auth Endpoints - Public Access")
    class AuthEndpointsTest {

        @Test
        @DisplayName("POST /api/auth/signup - should be accessible without authentication")
        void signupShouldBePublic() throws Exception {
            User user = User.builder()
                    .email("public-signup@mail.com")
                    .password("password123")
                    .role(Role.USER)
                    .build();

            mockMvc.perform(post("/api/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(user)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.email").value("public-signup@mail.com"));
        }

        @Test
        @DisplayName("POST /api/auth/signin - should be accessible without authentication")
        void signinShouldBePublic() throws Exception {
            User user = User.builder()
                    .email("public-signin@mail.com")
                    .password("password123")
                    .role(Role.USER)
                    .build();

            mockMvc.perform(post("/api/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(user)))
                    .andExpect(status().isCreated());

            AuthRequest authRequest = new AuthRequest("public-signin@mail.com", "password123");
            mockMvc.perform(post("/api/auth/signin")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(authRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").isNotEmpty());
        }

        @Test
        @DisplayName("POST /api/auth/signin - invalid credentials should return 401")
        void signinWithBadCredentialsShouldReturn401() throws Exception {
            AuthRequest authRequest = new AuthRequest("nonexistent@mail.com", "wrong");
            mockMvc.perform(post("/api/auth/signin")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(authRequest)))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ==================== UNAUTHENTICATED ACCESS ====================

    @Nested
    @DisplayName("Unauthenticated Access - Should Return 401")
    class UnauthenticatedAccessTest {

        @Test
        @DisplayName("GET /api/workspaces - unauthenticated should return 401")
        void workspacesShouldRequireAuth() throws Exception {
            mockMvc.perform(get("/api/workspaces"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/users - unauthenticated should return 401")
        void usersShouldRequireAuth() throws Exception {
            mockMvc.perform(get("/api/users"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/snapshots - unauthenticated should return 401")
        void snapshotsShouldRequireAuth() throws Exception {
            mockMvc.perform(get("/api/snapshots"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/prices - unauthenticated should return 401")
        void pricesShouldRequireAuth() throws Exception {
            mockMvc.perform(get("/api/prices"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("POST /api/workspaces - unauthenticated should return 401")
        void createWorkspaceShouldRequireAuth() throws Exception {
            mockMvc.perform(post("/api/workspaces")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("PUT /api/users/1 - unauthenticated should return 401")
        void updateUserShouldRequireAuth() throws Exception {
            mockMvc.perform(put("/api/users/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("DELETE /api/users/1 - unauthenticated should return 401")
        void deleteUserShouldRequireAuth() throws Exception {
            mockMvc.perform(delete("/api/users/1"))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ==================== ROLE-BASED ACCESS (UserController - ADMIN only) ====================

    @Nested
    @DisplayName("UserController - ADMIN Role Required")
    class UserEntityControllerRoleTest {

        @Test
        @DisplayName("GET /api/users - USER role should return 403")
        void regularUserCannotListUsers() throws Exception {
            SecurityUser regularUser = new SecurityUser(1L, "user@mail.com", "pass",
                    List.of(new SimpleGrantedAuthority("ROLE_USER")));

            mockMvc.perform(get("/api/users").with(user(regularUser)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("GET /api/users/{id} - USER role should return 403")
        void regularUserCannotGetUserById() throws Exception {
            SecurityUser regularUser = new SecurityUser(1L, "user@mail.com", "pass",
                    List.of(new SimpleGrantedAuthority("ROLE_USER")));

            mockMvc.perform(get("/api/users/1").with(user(regularUser)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("PUT /api/users/{id} - USER role should return 403")
        void regularUserCannotUpdateUser() throws Exception {
            SecurityUser regularUser = new SecurityUser(1L, "user@mail.com", "pass",
                    List.of(new SimpleGrantedAuthority("ROLE_USER")));

            User user = User.builder()
                    .email("updated@mail.com")
                    .password("newpass")
                    .role(Role.USER)
                    .build();

            mockMvc.perform(put("/api/users/1")
                            .with(user(regularUser))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(user)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("DELETE /api/users/{id} - USER role should return 403")
        void regularUserCannotDeleteUser() throws Exception {
            SecurityUser regularUser = new SecurityUser(1L, "user@mail.com", "pass",
                    List.of(new SimpleGrantedAuthority("ROLE_USER")));

            mockMvc.perform(delete("/api/users/1").with(user(regularUser)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("GET /api/users - ADMIN role should return 200")
        void adminCanListUsers() throws Exception {
            SecurityUser admin = new SecurityUser(1L, "admin@mail.com", "pass",
                    List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

            mockMvc.perform(get("/api/users").with(user(admin)))
                    .andExpect(status().isOk());
        }
    }

    // ==================== AUTHENTICATED ACCESS (Workspace, Snapshot, Price) ====================

    @Nested
    @DisplayName("Authenticated Access - Any Role Allowed")
    class AuthenticatedAccessTest {

        @Test
        @DisplayName("GET /api/workspaces - authenticated USER should return 200")
        void authenticatedUserCanAccessWorkspaces() throws Exception {
            SecurityUser regularUser = new SecurityUser(1L, "user@mail.com", "pass",
                    List.of(new SimpleGrantedAuthority("ROLE_USER")));

            mockMvc.perform(get("/api/workspaces").with(user(regularUser)))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("GET /api/snapshots - authenticated USER should return 200")
        void authenticatedUserCanAccessSnapshots() throws Exception {
            SecurityUser regularUser = new SecurityUser(1L, "user@mail.com", "pass",
                    List.of(new SimpleGrantedAuthority("ROLE_USER")));

            mockMvc.perform(get("/api/snapshots").with(user(regularUser)))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("GET /api/prices - authenticated USER should return 200")
        void authenticatedUserCanAccessPrices() throws Exception {
            SecurityUser regularUser = new SecurityUser(1L, "user@mail.com", "pass",
                    List.of(new SimpleGrantedAuthority("ROLE_USER")));

            mockMvc.perform(get("/api/prices").with(user(regularUser)))
                    .andExpect(status().isOk());
        }
    }

    // ==================== JWT TOKEN INTEGRATION ====================

    @Nested
    @DisplayName("JWT Token Integration")
    class JwtTokenIntegrationTest {

        @Test
        @DisplayName("Valid JWT token should grant access to protected endpoints")
        void validJwtTokenShouldGrantAccess() throws Exception {
            String token = signUpAndGetToken("jwt-user@mail.com", "password123", Role.USER);

            mockMvc.perform(get("/api/workspaces")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Invalid JWT token should return 401")
        void invalidJwtTokenShouldReturn401() throws Exception {
            mockMvc.perform(get("/api/workspaces")
                            .header("Authorization", "Bearer invalid.token.here"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Expired/malformed Bearer header should return 401")
        void malformedBearerShouldReturn401() throws Exception {
            mockMvc.perform(get("/api/workspaces")
                            .header("Authorization", "Bearer "))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("ADMIN JWT token should access admin-only endpoints")
        void adminJwtTokenShouldAccessAdminEndpoints() throws Exception {
            String token = signUpAndGetToken("jwt-admin@mail.com", "password123", Role.ADMIN);

            mockMvc.perform(get("/api/users")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("USER JWT token should be forbidden from admin-only endpoints")
        void userJwtTokenShouldBeForbiddenFromAdminEndpoints() throws Exception {
            String token = signUpAndGetToken("jwt-regular@mail.com", "password123", Role.USER);

            mockMvc.perform(get("/api/users")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isForbidden());
        }
    }
}
