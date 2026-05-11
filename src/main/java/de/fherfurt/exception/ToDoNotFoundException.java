package de.fherfurt.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * Exception thrown when a specific ToDo entity cannot be found in the system.
 *
 * <p>This exception typically results in a 404 Not Found HTTP response
 * when caught by the {@link GlobalExceptionHandler}.</p>
 */
@Slf4j
public class ToDoNotFoundException extends RuntimeException {
  public ToDoNotFoundException(Long id) {
    super("Todo with ID " + id + " was not found.");
    log.warn("Todo with ID {} not found", id);
  }
}
