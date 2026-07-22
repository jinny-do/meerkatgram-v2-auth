package com.meerkatgramv2auth.global.security.constant;

import lombok.Getter;

@Getter
public enum RolePolicy {
    NORMAL("NORMAL")
    ,SUPER("SUPER");

    // provider : 문자열 저장할 필드
    private final String role;

    RolePolicy(String role) {
        this.role = role;
    }

}