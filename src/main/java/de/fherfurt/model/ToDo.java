package de.fherfurt.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Data model representing a single ToDo item within the application.
 *
 * <p>This class holds the state of a task, including its identification,
 * title, detailed description, and completion status. It includes validation
 * constraints to ensure data integrity.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ToDo {

  private Long id;

  @NotBlank(message = "Title cannot be empty")
  @Size(max = 100, message = "Title can have a maximum of 100 characters")
  private String title;

  @Size(max = 500, message = "Description can have a maximum of 500 characters")
  private String description;

  private boolean completed;
}
