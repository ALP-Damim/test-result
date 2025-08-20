package com.kt.damim.testresult.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


/**
 * 전역 예외 처리기.
 * - 컨트롤러에서 던져진 예외를 한 곳에서 JSON 형태로 응답.
 * - 예외 유형별로 HTTP 상태코드/메시지를 표준화.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("잘못된 요청 발생: {}", e.getMessage(), e);

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Bad Request");
        response.put("message", e.getMessage());

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception e) {
        // 스택 트레이스 포함 로그
        log.error("예상치 못한 오류 발생: {} - {}", e.getClass().getName(), e.getMessage(), e);

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("error", "Internal Server Error");

        // 클라이언트 응답에는 디테일을 다 보여주면 보안상 위험 → message에는 축약만
        response.put("message", "서버 내부 오류가 발생했습니다.");

        // 추가적으로 서버 디버깅용 상세 필드 (원한다면)
        response.put("exception", e.getClass().getSimpleName());
        response.put("detail", e.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
