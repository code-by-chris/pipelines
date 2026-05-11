package de.fherfurt.unit;

import de.fherfurt.exception.ToDoNotFoundException;
import de.fherfurt.model.ToDo;
import de.fherfurt.service.ToDoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ToDoService.
 * <p>
 * The service is tested in isolation – without Spring context.
 * Since the service has no external dependencies (in-memory),
 * no mocking is required.
 */
class ToDoServiceTest {

  private ToDoService toDoService;

  /**
   * Runs BEFORE EACH test.
   * Every test starts with a fresh, empty service instance.
   */
  @BeforeEach
  void setup() {
    toDoService = new ToDoService();
  }

  // ─────────────────────────────────────────
  // getAllToDos()
  // ─────────────────────────────────────────

  @Test
  @DisplayName("getAllToDos() -> returns an empty list when no toDos exist")
  void getAllToDos_shouldReturnEmptyListWhenNoToDoExist() {
    List<ToDo> toDos = toDoService.getAllToDos();

    assertNotNull(toDos);
    assertTrue(toDos.isEmpty());
  }

  @Test
  @DisplayName("getAllToDos() → returns all existing todos")
  void getAllToDos_shouldReturnAllExistingToDos() {
    toDoService.createToDo(new ToDo(null, "Shopping", "Buy milk", false));
    toDoService.createToDo(new ToDo(null, "Sport", "Go running", false));

    List<ToDo> toDos = toDoService.getAllToDos();

    assertEquals(2, toDos.size());
  }

  @Test
  @DisplayName("getAllToDos() -> returns an copy, not the original list")
  void getAllToDos_shouldReturnCopyListNoTheOriginalToDosList() {
    toDoService.createToDo(new ToDo(null, "Test", "Test", false));

    List<ToDo> toDos = toDoService.getAllToDos();
    toDos.clear();

    assertEquals(1, toDoService.getAllToDos().size());
  }

  // ─────────────────────────────────────────
  // getToDoById()
  // ─────────────────────────────────────────

  @Test
  @DisplayName("getToDoById() → returns correct todo")
  void getToDoById_shouldReturnCorrectToDo() {
    ToDo result = toDoService.createToDo(new ToDo(null, "Shopping", "Buy milk", false));
    ToDo toDo = toDoService.getToDoById(result.getId());

    assertEquals("Shopping", result.getTitle());
    assertEquals("Buy milk", result.getDescription());
    assertFalse(result.isCompleted());
  }

  @Test
  @DisplayName("getToDoById() → throws exception when todo not found")
  void getToDoById_shouldThrowException_whenToDoNotFound() {
    assertThrows(ToDoNotFoundException.class, () ->
        toDoService.getToDoById(999L)
    );
  }

  @Test
  @DisplayName("getToDoById() → exception message contains the ID")
  void getToDoById_shouldContainIdInExceptionMessage() {
    ToDoNotFoundException exception = assertThrows(ToDoNotFoundException.class, () ->
        toDoService.getToDoById(42L)
    );

    assertTrue(exception.getMessage().contains("42"));
  }

  @Test
  @DisplayName("createToDo() → todo is saved with generated ID")
  void createToDo_shouldAssignIdAndSaveToDo() {
    ToDo toDo = new ToDo(null, "Learn", "Learn Java", false);

    ToDo result = toDoService.createToDo(toDo);

    assertNotNull(result.getId());
    assertEquals("Learn", result.getTitle());
    assertEquals(1, toDoService.getAllToDos().size());
  }

  @Test
  @DisplayName("createToDo() → IDs are automatically incremented")
  void createToDo_shouldIncrementIds() {
    ToDo first = toDoService.createToDo(new ToDo(null, "First", "", false));
    ToDo second = toDoService.createToDo(new ToDo(null, "Second", "", false));

    assertEquals(1L, first.getId());
    assertEquals(2L, second.getId());
  }

  @Test
  @DisplayName("createToDo() → multiple todos can be created")
  void createToDo_shouldAllowMultipleToDos() {
    toDoService.createToDo(new ToDo(null, "First", "", false));
    toDoService.createToDo(new ToDo(null, "Second", "", false));
    toDoService.createToDo(new ToDo(null, "Third", "", false));

    assertEquals(3, toDoService.getAllToDos().size());
  }

  // ─────────────────────────────────────────
  // updateToDo()
  // ─────────────────────────────────────────

  @Test
  @DisplayName("updateToDo() → all fields are updated correctly")
  void updateToDo_shouldUpdateAllFields() {
    ToDo created = toDoService.createToDo(new ToDo(null, "Old", "Old description", false));

    ToDo updated = toDoService.updateToDo(
        created.getId(),
        new ToDo(null, "New", "New description", true)
    );

    assertEquals("New", updated.getTitle());
    assertEquals("New description", updated.getDescription());
    assertTrue(updated.isCompleted());
  }

  @Test
  @DisplayName("updateToDo() → throws exception when todo not found")
  void updateToDo_shouldThrowException_whenToDoNotFound() {
    ToDo toDo = new ToDo(null, "Test", "", false);

    assertThrows(ToDoNotFoundException.class, () ->
        toDoService.updateToDo(999L, toDo)
    );
  }

  @Test
  @DisplayName("updateToDo() → updated todo is retrievable by ID")
  void updateToDo_shouldPersistChanges() {
    ToDo created = toDoService.createToDo(new ToDo(null, "Original", "", false));

    toDoService.updateToDo(created.getId(), new ToDo(null, "Updated", "New", true));
    ToDo retrieved = toDoService.getToDoById(created.getId());

    assertEquals("Updated", retrieved.getTitle());
    assertTrue(retrieved.isCompleted());
  }

  // ─────────────────────────────────────────
  // deleteToDo()
  // ─────────────────────────────────────────

  @Test
  @DisplayName("deleteToDo() → todo is removed from the list")
  void deleteToDo_shouldRemoveToDoFromList() {
    ToDo created = toDoService.createToDo(new ToDo(null, "Delete me", "", false));

    toDoService.deleteToDo(created.getId());

    assertTrue(toDoService.getAllToDos().isEmpty());
  }

  @Test
  @DisplayName("deleteToDo() → throws exception when todo not found")
  void deleteToDo_shouldThrowException_whenToDoNotFound() {
    assertThrows(ToDoNotFoundException.class, () ->
        toDoService.deleteToDo(999L)
    );
  }

  @Test
  @DisplayName("deleteToDo() → only deletes the correct todo")
  void deleteToDo_shouldOnlyDeleteCorrectToDo() {
    ToDo first = toDoService.createToDo(new ToDo(null, "First", "", false));
    toDoService.createToDo(new ToDo(null, "Second", "", false));

    toDoService.deleteToDo(first.getId());

    List<ToDo> remaining = toDoService.getAllToDos();
    assertEquals(1, remaining.size());
    assertEquals("Second", remaining.get(0).getTitle());
  }

  @Test
  @DisplayName("deleteToDo() → deleted todo is no longer retrievable")
  void deleteToDo_shouldMakeToDoUnretrievable() {
    ToDo created = toDoService.createToDo(new ToDo(null, "Delete me", "", false));
    Long id = created.getId();

    toDoService.deleteToDo(id);

    assertThrows(ToDoNotFoundException.class, () ->
        toDoService.getToDoById(id)
    );
  }
}
