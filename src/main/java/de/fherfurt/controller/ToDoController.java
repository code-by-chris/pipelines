package de.fherfurt.controller;

import de.fherfurt.model.ToDo;
import de.fherfurt.service.ToDoService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing ToDo resources.
 *
 * <p>This class provides API endpoints for CRUD operations and acts as an
 * intermediary between the client and the business logic in the {@link ToDoService}.</p>
 */
@Slf4j
@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class ToDoController {

  private final ToDoService toDoService;

  /**
   * GET /api/todos – Retrieves all todos.
   */
  @GetMapping
  public ResponseEntity<List<ToDo>> getAllToDos() {
    log.info("GET /api/todos called");
    return ResponseEntity.ok(toDoService.getAllToDos());
  }

  /**
   * GET /api/todos/{id} – Retrieves a single todo by its ID.
   */
  @GetMapping("/{id}")
  public ResponseEntity<ToDo> getToDoById(@PathVariable Long id) {
    log.info("GET /api/todos/{} called", id);
    return ResponseEntity.ok(toDoService.getToDoById(id));
  }

  /**
   * POST /api/todos – Creates a new todo.
   */
  @PostMapping
  public ResponseEntity<ToDo> createToDo(@Valid @RequestBody ToDo toDo) {
    log.info("POST /api/todos called with title {}", toDo.getTitle());
    ToDo createdToDo = toDoService.createToDo(toDo);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdToDo);
  }

  /**
   * PUT /api/todos/{id} – Updates an existing todo.
   */
  @PutMapping("/{id}")
  public ResponseEntity<ToDo> updateToDo(@PathVariable Long id, @Valid @RequestBody ToDo toDo) {
    log.info("PUT /api/todos/{} called", id);
    return ResponseEntity.ok(toDoService.updateToDo(id, toDo));
  }

  /**
   * DELETE /api/todos/{id} – Deletes a todo by its ID.
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteToDo(@PathVariable Long id) {
    log.info("DELETE /api/todos/{} called", id);
    toDoService.deleteToDo(id);
    return ResponseEntity.noContent().build();
  }
}