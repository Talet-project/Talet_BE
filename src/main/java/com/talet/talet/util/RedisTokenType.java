package com.talet.talet.util;

import lombok.Getter;

import java.time.Duration;

public enum RedisTokenType {
    SIGN_UP_TOKEN("signUp", Duration.ofDays(1)),
    ACCESS_TOKEN("access", Duration.ofDays(7)),
    REFRESH_TOKEN("refresh", Duration.ofDays(30)),
    ADMIN_TOKEN("admin", Duration.ofHours(1)),;
    private final String prefix;
    @Getter
    private final Duration duration;

    RedisTokenType(String prefix, Duration duration) {
        this.prefix = prefix;
        this.duration = duration;
    }

    public String getKey(String identifier) {
        return prefix + ":" + identifier;
    }

    public String getTokenTypeClaim() {
        return this.name(); // JWT 클레임용: "SIGN_UP", "ACCESS", "REFRESH"
    }

}
