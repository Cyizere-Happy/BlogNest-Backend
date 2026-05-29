package com.blognest.config;

import java.util.UUID;

public class UserContext {
    private static final ThreadLocal<UUID> currentUser = new ThreadLocal<>();

    public static void setCurrentUserId(UUID userId) {
        currentUser.set(userId);
    }

    public static UUID getCurrentUserId() {
        return currentUser.get();
    }

    public static void clear() {
        currentUser.remove();
    }
}
