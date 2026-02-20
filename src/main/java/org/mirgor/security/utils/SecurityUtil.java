package org.mirgor.security.utils;

import org.mirgor.security.principal.SecurityUser;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtil {

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

    public static String getAuthority() {
        var principal = getPrincipal();

        if (principal instanceof SecurityUser userDetails) {
            return userDetails.getAuthorities().stream().findFirst().orElseThrow(IllegalArgumentException::new).getAuthority();
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
