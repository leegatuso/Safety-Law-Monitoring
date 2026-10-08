package com.company.safetylaw.support;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

public final class Checks {
    private Checks() {}

    public static <T> T found(T value) {
        if (value == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "항목을 찾을 수 없습니다.");
        return value;
    }

    public static String now() {
        return Instant.now().toString();
    }

    public static String text(String value) {
        return value == null ? "" : value;
    }

    public static void link(String value) {
        if (value != null && !value.isBlank() && !value.matches("https?://[^\\s]+"))
            throw new IllegalArgumentException("링크는 HTTP(S) 주소로 입력하세요.");
    }
}
