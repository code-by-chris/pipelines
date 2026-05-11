package de.fherfurt.e2e;

import de.fherfurt.ToDoApplication;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * E2E tests for the ToDo API.
 * <p>
 * These tests start the complete Spring Boot application
 * and send real HTTP requests against the running API.
 * This simulates the behavior of a real user.
 * <p>
 * RANDOM_PORT → Spring starts on a random port so no conflicts occur.
 */
@SpringBootTest(
    classes = ToDoApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class ToDoApiE2ETest {

  @LocalServerPort
  private int port;

  @BeforeEach
  void setUp() {
    RestAssured.port = port;
    RestAssured.basePath = "/api/todos";
  }

  // ─────────────────────────────────────────
  // E2E Test 1: GET /api/todos → 200
  // ─────────────────────────────────────────

  @Test
  @DisplayName("E2E: GET /api/todos → returns HTTP 200 and a list")
  void getAllToDos_shouldReturn200WithList() {
    // First create a todo so the list is not empty
    String body = """
        {
            "title": "E2E Test Todo",
            "description": "Created by E2E test",
            "completed": false
        }
        """;

    given()
        .contentType(ContentType.JSON)
        .body(body)
        .post();

    given()
        .when()
        .get()
        .then()
        .statusCode(200)
        .body("$", hasSize(greaterThan(0)));
  }

  // ─────────────────────────────────────────
  // E2E Test 2: POST /api/todos → 201 + content check
  // ─────────────────────────────────────────

  @Test
  @DisplayName("E2E: POST /api/todos → creates todo and returns HTTP 201")
  void createToDo_shouldReturn201WithCreatedToDo() {
    String body = """
        {
            "title": "New Todo",
            "description": "Todo description",
            "completed": false
        }
        """;

    given()
        .contentType(ContentType.JSON)
        .body(body)
        .when()
        .post()
        .then()
        .statusCode(201)
        .body("id", notNullValue())
        .body("title", equalTo("New Todo"))
        .body("description", equalTo("Todo description"))
        .body("completed", equalTo(false));
  }

  // ─────────────────────────────────────────
  // E2E Test 3: GET /api/todos/9999 → 404
  // ─────────────────────────────────────────

  @Test
  @DisplayName("E2E: GET /api/todos/9999 → returns HTTP 404")
  void getToDoById_shouldReturn404_whenToDoNotFound() {
    given()
        .when()
        .get("/9999")
        .then()
        .statusCode(404)
        .body("error", notNullValue());
  }

  // ─────────────────────────────────────────
  // E2E Test 4: PUT /api/todos/{id} → 200 + updated values
  // ─────────────────────────────────────────

  @Test
  @DisplayName("E2E: PUT /api/todos/{id} → updates todo and returns HTTP 200")
  void updateToDo_shouldReturn200WithUpdatedToDo() {
    // First create a todo
    String createBody = """
        {
            "title": "Original Title",
            "description": "Original description",
            "completed": false
        }
        """;

    int id = given()
        .contentType(ContentType.JSON)
        .body(createBody)
        .post()
        .then()
        .extract()
        .path("id");

    // Then update it
    String updateBody = """
        {
            "title": "Updated Title",
            "description": "New description",
            "completed": true
        }
        """;

    given()
        .contentType(ContentType.JSON)
        .body(updateBody)
        .when()
        .put("/" + id)
        .then()
        .statusCode(200)
        .body("title", equalTo("Updated Title"))
        .body("description", equalTo("New description"))
        .body("completed", equalTo(true));
  }

  // ─────────────────────────────────────────
  // E2E Test 5: DELETE /api/todos/{id} → 204
  // ─────────────────────────────────────────

  @Test
  @DisplayName("E2E: DELETE /api/todos/{id} → deletes todo and returns HTTP 204")
  void deleteToDo_shouldReturn204() {
    // First create a todo
    String body = """
        {
            "title": "To be deleted",
            "description": "",
            "completed": false
        }
        """;

    int id = given()
        .contentType(ContentType.JSON)
        .body(body)
        .post()
        .then()
        .extract()
        .path("id");

    // Delete it
    given()
        .when()
        .delete("/" + id)
        .then()
        .statusCode(204);

    // Verify it's really gone
    given()
        .when()
        .get("/" + id)
        .then()
        .statusCode(404);
  }

  // ─────────────────────────────────────────
  // E2E Test 6: POST mit leerem Titel → 400
  // ─────────────────────────────────────────

  @Test
  @DisplayName("E2E: POST /api/todos with empty title → returns HTTP 400")
  void createToDo_shouldReturn400_whenTitleIsBlank() {
    String body = """
        {
            "title": "",
            "description": "No title",
            "completed": false
        }
        """;

    given()
        .contentType(ContentType.JSON)
        .body(body)
        .when()
        .post()
        .then()
        .statusCode(400);
  }

  // ─────────────────────────────────────────
  // E2E Test 7: GET nach Create → korrekte Daten
  // ─────────────────────────────────────────

  @Test
  @DisplayName("E2E: GET /api/todos/{id} → returns correct todo after creation")
  void getToDoById_shouldReturnCorrectToDo_afterCreation() {
    String body = """
        {
            "title": "Specific Todo",
            "description": "Specific description",
            "completed": false
        }
        """;

    int id = given()
        .contentType(ContentType.JSON)
        .body(body)
        .post()
        .then()
        .extract()
        .path("id");

    given()
        .when()
        .get("/" + id)
        .then()
        .statusCode(200)
        .body("title", equalTo("Specific Todo"))
        .body("description", equalTo("Specific description"));
  }
}