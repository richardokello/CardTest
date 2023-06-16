package com.rich.app.card.users.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {
    ADMIN_READ("admin:read"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_CREATE("admin:create"),
    ADMIN_DELETE("admin:delete"),
    MEMBER_READ("management:read"),
    MEMBER_UPDATE("management:update"),
    MEMBER_CREATE("management:create"),
    MEMBER_DELETE("management:delete")

    ;
    @Getter
    private final String permission;
}
