package de.fherfurt.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fherfurt.controller.ToDoController;
import de.fherfurt.exception.GlobalExceptionHandler;
import de.fherfurt.exception.ToDoNotFoundException;
import de.fherfurt.model.ToDo;
import de.fherfurt.service.ToDoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for ToDoController.
 * <p>
 * MockMvc simulates HTTP requests without starting a real server.
 * ToDoService is mocked with Mockito – only the Controller logic is tested here.
 * <p>
 * Mock       → creates a mock of ToDoService (no real logic)
 * InjectMocks → injects the mock into ToDoController
 */
@ExtendWith(MockitoExtension.class)
class ToDoControllerTest {

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  @Mock
  private ToDoService toDoService;

  @InjectMocks
  private ToDoController toDoController;

  @BeforeEach
  void setUp() {
    // MockMvc setup with GlobalExceptionHandler so 404/400 responses work correctly
    mockMvc = MockMvcBuilders
        .standaloneSetup(toDoController)
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();
    objectMapper = new ObjectMapper();
  }

  // ─────────────────────────────────────────
  // GET /api/todos
  // ─────────────────────────────────────────

  @Test
  @DisplayName("GET /api/todos → 200 and returns list of todos")
  void getAllToDos_shouldReturn200WithList() throws Exception {
    List<ToDo> toDos = List.of(
        new ToDo(1L, "Shopping", "Buy milk", false),
        new ToDo(2L, "Sport", "Go running", true)
    );
    when(toDoService.getAllToDos()).thenReturn(toDos);

    mockMvc.perform(get("/api/todos"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].title").value("Shopping"))
        .andExpect(jsonPath("$[1].title").value("Sport"));
  }

  @Test
  @DisplayName("GET /api/todos → 200 and returns empty list when no todos exist")
  void getAllToDos_shouldReturn200WithEmptyList() throws Exception {
    when(toDoService.getAllToDos()).thenReturn(List.of());

    mockMvc.perform(get("/api/todos"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));
  }

  // ─────────────────────────────────────────
  // GET /api/todos/{id}
  // ─────────────────────────────────────────

  @Test
  @DisplayName("GET /api/todos/{id} → 200 and returns correct todo")
  void getToDoById_shouldReturn200WithToDo() throws Exception {
    ToDo toDo = new ToDo(1L, "Shopping", "Buy milk", false);
    when(toDoService.getToDoById(1L)).thenReturn(toDo);

    mockMvc.perform(get("/api/todos/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.title").value("Shopping"))
        .andExpect(jsonPath("$.description").value("Buy milk"))
        .andExpect(jsonPath("$.completed").value(false));
  }

  @Test
  @DisplayName("GET /api/todos/{id} → 404 when todo not found")
  void getToDoById_shouldReturn404_whenToDoNotFound() throws Exception {
    when(toDoService.getToDoById(999L)).thenThrow(new ToDoNotFoundException(999L));

    mockMvc.perform(get("/api/todos/999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").exists());
  }

  // ─────────────────────────────────────────
  // POST /api/todos
  // ─────────────────────────────────────────

  @Test
  @DisplayName("POST /api/todos → 201 and returns created todo")
  void createToDo_shouldReturn201WithCreatedToDo() throws Exception {
    ToDo input = new ToDo(null, "New Todo", "Description", false);
    ToDo saved = new ToDo(1L, "New Todo", "Description", false);
    when(toDoService.createToDo(any(ToDo.class))).thenReturn(saved);

    mockMvc.perform(post("/api/todos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(input)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.title").value("New Todo"));
  }

  @Test
  @DisplayName("POST /api/todos → 400 when title is blank")
  void createToDo_shouldReturn400_whenTitleIsBlank() throws Exception {
    ToDo invalid = new ToDo(null, "", "Description", false);

    mockMvc.perform(post("/api/todos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalid)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("POST /api/todos → 400 when title is null")
  void createToDo_shouldReturn400_whenTitleIsNull() throws Exception {
    ToDo invalid = new ToDo(null, null, "Description", false);

    mockMvc.perform(post("/api/todos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalid)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("POST /api/todos → service is called exactly once")
  void createToDo_shouldCallServiceOnce() throws Exception {
    ToDo input = new ToDo(null, "New Todo", "", false);
    ToDo saved = new ToDo(1L, "New Todo", "", false);
    when(toDoService.createToDo(any(ToDo.class))).thenReturn(saved);

    mockMvc.perform(post("/api/todos")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(input)));

    verify(toDoService, times(1)).createToDo(any(ToDo.class));
  }

  // ─────────────────────────────────────────
  // PUT /api/todos/{id}
  // ─────────────────────────────────────────

  @Test
  @DisplayName("PUT /api/todos/{id} → 200 and returns updated todo")
  void updateToDo_shouldReturn200WithUpdatedToDo() throws Exception {
    ToDo update = new ToDo(null, "Updated Title", "Updated Desc", true);
    ToDo updated = new ToDo(1L, "Updated Title", "Updated Desc", true);
    when(toDoService.updateToDo(eq(1L), any(ToDo.class))).thenReturn(updated);

    mockMvc.perform(put("/api/todos/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(update)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Updated Title"))
        .andExpect(jsonPath("$.completed").value(true));
  }

  @Test
  @DisplayName("PUT /api/todos/{id} → 404 when todo not found")
  void updateToDo_shouldReturn404_whenToDoNotFound() throws Exception {
    ToDo update = new ToDo(null, "Updated", "", false);
    when(toDoService.updateToDo(eq(999L), any(ToDo.class)))
        .thenThrow(new ToDoNotFoundException(999L));

    mockMvc.perform(put("/api/todos/999")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(update)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("PUT /api/todos/{id} → 400 when title is blank")
  void updateToDo_shouldReturn400_whenTitleIsBlank() throws Exception {
    ToDo invalid = new ToDo(null, "", "", false);

    mockMvc.perform(put("/api/todos/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalid)))
        .andExpect(status().isBadRequest());
  }

  // ─────────────────────────────────────────
  // DELETE /api/todos/{id}
  // ─────────────────────────────────────────

  @Test
  @DisplayName("DELETE /api/todos/{id} → 204 when todo deleted")
  void deleteToDo_shouldReturn204() throws Exception {
    doNothing().when(toDoService).deleteToDo(1L);

    mockMvc.perform(delete("/api/todos/1"))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("DELETE /api/todos/{id} → 404 when todo not found")
  void deleteToDo_shouldReturn404_whenToDoNotFound() throws Exception {
    doThrow(new ToDoNotFoundException(999L)).when(toDoService).deleteToDo(999L);

    mockMvc.perform(delete("/api/todos/999"))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("DELETE /api/todos/{id} → service is called exactly once")
  void deleteToDo_shouldCallServiceOnce() throws Exception {
    doNothing().when(toDoService).deleteToDo(1L);

    mockMvc.perform(delete("/api/todos/1"));

    verify(toDoService, times(1)).deleteToDo(1L);
  }
}