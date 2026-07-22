package com.meerkatgramv2auth.global.error.custom;

public class ForbiddenException extends RuntimeException {
    // 커스텀 에러는 주로 RuntimeException 상속받음
    public ForbiddenException(String message) {
        super(message);
    }
}
