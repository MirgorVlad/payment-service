package org.mirgor.security.utils;

import org.mirgor.security.principal.SecurityUser;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtil {

    private SecurityUtil() {
    }

    public static Long getCurrentUserId() {
        var authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user");
        }

        var principal = authentication.getPrincipal();

        if (principal instanceof SecurityUser userDetails) {
            return userDetails.getId();
        }
        throw new IllegalStateException(
                "Unsupported authentication principal: " + principal.getClass()
        );
    }

}
