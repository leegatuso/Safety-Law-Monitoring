package com.company.safetylaw.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestControllerAdvice
public class ApiErrors {
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    ResponseEntity<?> validation(org.springframework.web.bind.MethodArgumentNotValidException ex) {
        var error = ex.getBindingResult().getFieldErrors().stream().findFirst();
        return ResponseEntity.badRequest()
                .body(
                        Map.of(
                                "detail",
                                error.map(e -> e.getField() + ": " + e.getDefaultMessage())
                                        .orElse("입력값을 확인하세요.")));
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    ResponseEntity<?> unreadable() {
        return ResponseEntity.badRequest().body(Map.of("detail", "입력값의 형식을 확인하세요."));
    }

    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<?> status(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode())
                .body(
                        Map.of(
                                "detail",
                                ex.getReason() == null ? "요청을 처리하지 못했습니다." : ex.getReason()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<?> invalid(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("detail", ex.getMessage()));
    }

    @ExceptionHandler(java.time.DateTimeException.class)
    ResponseEntity<?> invalidDate() {
        return ResponseEntity.badRequest()
                .body(Map.of("detail", "날짜 형식을 확인하세요. YYYY-MM-DD 형식으로 입력하세요."));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<?> conflict(DataIntegrityViolationException ex) {
        return ResponseEntity.status(409)
                .body(Map.of("detail", "중복되거나 다른 항목에서 참조 중인 데이터입니다. 입력 길이도 확인하세요."));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    ResponseEntity<?> tooLarge() {
        return ResponseEntity.status(413).body(Map.of("detail", "PDF는 20MB 이하만 첨부할 수 있습니다."));
    }
}
