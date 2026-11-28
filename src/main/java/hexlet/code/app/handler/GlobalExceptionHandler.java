package hexlet.code.app.handler;

import hexlet.code.app.exception.InvalidRequestException;
import hexlet.code.app.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidRequest(HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(
            HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, request);
    }

    private ResponseEntity<Map<String, Object>> buildResponse(
            HttpStatus status, HttpServletRequest request) {

        return ResponseEntity.status(status)
                .body(
                        Map.of(
                                "timestamp", Instant.now().toString(),
                                "status", status.value(),
                                "error", status.getReasonPhrase(),
                                "path", request.getRequestURI()));
    }
}
