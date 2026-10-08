package com.company.safetylaw.support;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/** Shared helpers for the existing map-based API payloads. */
public final class Rows {
    private Rows() {}

    public static String now() {
        return Instant.now().toString();
    }

    public static String str(Map<String, Object> row, String key) {
        return Objects.toString(row.get(key), "");
    }

    public static long id(Map<String, Object> row) {
        return ((Number) row.get("id")).longValue();
    }

    public static boolean same(Object a, Object b) {
        return Objects.toString(a, "").equals(Objects.toString(b, ""));
    }

    public static Map<String, Object> row(Object... values) {
        var row = new LinkedHashMap<String, Object>();
        for (int i = 0; i < values.length; i += 2) row.put((String) values[i], values[i + 1]);
        return row;
    }
}
