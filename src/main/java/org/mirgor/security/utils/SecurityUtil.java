package org.mirgor.security.utils;

import org.mirgor.common.constant.Role;
import org.mirgor.security.principal.SecurityUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

public final class SecurityUtil {

    public static final String ROLE_PREFIX = "ROLE_";

    private SecurityUtil() {
    }

    public static Long getCurrentUserId() {
        var principal = getPrincipal();

        if (principal instanceof SecurityUser userDetails) {
            return userDetails.getId();
        }
        throw new IllegalStateException(
                "Unsupported authentication principal: " + principal.getClass()
        );
    }

    public static Role getCurrentUserRole() {
        var principal = getPrincipal();

        if (principal instanceof SecurityUser userDetails) {
            var authority = userDetails.getAuthorities().stream().findFirst().orElseThrow(IllegalArgumentException::new)
                    .getAuthority();
            return Role.valueOf(authority.replace(ROLE_PREFIX, ""));
        }
        throw new IllegalStateException(
                "Unsupported authentication principal: " + principal.getClass()
        );
    }

    private static Object getPrincipal() {
        var authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user");
        }

        return authentication.getPrincipal();
    }

}
