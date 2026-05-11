package de.fherfurt.exception;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler that intercepts exceptions across the entire application.
 *
 * <p>This class provides centralized exception handling by converting various
 * exceptions into consistent HTTP error responses.</p>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Handles the case where a todo is not found → 404.
   */
  @ExceptionHandler(ToDoNotFoundException.class)
  public ResponseEntity<Map<String, String>> handleNotFound(ToDoNotFoundException e) {
    log.error("ToDoNotFoundException: {} ", e.getMessage());
    Map<String, String> errorResponse = new HashMap<>();
    errorResponse.put("error", e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  /**
   * Handles validation errors (e.g. empty title) → 400.
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException e) {
    log.error("Validation error: {} ", e.getMessage());
    Map<String, String> errorResponse = new HashMap<>();
    e.getBindingResult().getFieldErrors().forEach(fieldError ->
        errorResponse.put(fieldError.getField(), fieldError.getDefaultMessage())
    );
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }
}
