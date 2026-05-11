package de.fherfurt.service;

import de.fherfurt.exception.ToDoNotFoundException;
import de.fherfurt.model.ToDo;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service layer responsible for handling the business logic of the ToDo application.
 *
 * <p>This implementation uses an in-memory data store for managing ToDo entities.
 * It provides methods for standard CRUD operations and ensures data consistency
 * during the application's runtime.</p>
 */
@Slf4j
@Service
public class ToDoService {

  private final List<ToDo> toDos = new ArrayList<>();
  private final AtomicLong counter = new AtomicLong(1);

  /**
   * Returns all existing todos.
   */
  public List<ToDo> getAllToDos() {
    log.info("Get all todos, count: {}", toDos.size());
    return new ArrayList<>(toDos);
  }

  /**
   * Searches a todo by its ID.
   * Throws TodoNotFoundException if not found.
   */
  public ToDo getToDoById(Long id) {
    log.info("Looking for Todo with ID {}", id);
    return toDos.stream()
        .filter(toDo -> toDo.getId().equals(id))
        .findFirst()
        .orElseThrow(() -> new ToDoNotFoundException(id));
  }

  /**
   * Creates a new todo and saves it.
   */
  public ToDo createToDo(ToDo toDo) {
    toDo.setId(counter.getAndIncrement());
    toDos.add(toDo);
    log.info("Todo created with ID: {}", toDo.getId());
    return toDo;
  }

  /**
   * Updates an existing todo.
   * Throws TodoNotFoundException if not found.
   */
  public ToDo updateToDo(Long id, ToDo updatedToDo) {
    ToDo existingToDo = getToDoById(id);
    existingToDo.setTitle(updatedToDo.getTitle());
    existingToDo.setDescription(updatedToDo.getDescription());
    existingToDo.setCompleted(updatedToDo.isCompleted());
    log.info("Todo updated with ID {}", id);
    return existingToDo;
  }

  /**
   * Deletes a todo based on its ID.
   * Throws TodoNotFoundException if not found.
   */
  public void deleteToDo(Long id) {
    ToDo existingToDo = getToDoById(id);
    toDos.remove(existingToDo);
    log.info("Todo with ID {} deleted", id);
  }
}
