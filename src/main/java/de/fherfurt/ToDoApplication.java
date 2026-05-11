package de.fherfurt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class for initializing the Spring Boot application.
 */
@SpringBootApplication
public class ToDoApplication {

  public static void main(String[] args) {
    SpringApplication.run(ToDoApplication.class, args);
  }
}
