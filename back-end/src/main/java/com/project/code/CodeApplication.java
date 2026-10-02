package com.project.code;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point of the Spring Boot application.
 *
 * <p>The {@code @SpringBootApplication} annotation enables auto-configuration,
 * component scanning (starting from the current package and descending into
 * subpackages), and configuration support.</p>
 */
@SpringBootApplication
public class CodeApplication {

	/**
	 * Launches the Spring Boot application.
	 *
	 * @param args command-line arguments passed at startup
	 */
	public static void main(String[] args) {
		SpringApplication.run(CodeApplication.class, args);
	}
}