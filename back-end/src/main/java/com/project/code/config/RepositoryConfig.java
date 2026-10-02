package com.project.code.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Configuration class that defines the base packages for the two
 * Spring Data modules used by the application.
 *
 * <p>Both Spring Data JPA and Spring Data MongoDB coexist on the classpath.
 * In this situation, Spring Boot activates a strict repository configuration
 * mode. Each module scans every candidate interface and discards those
 * that do not belong to it. This produces a large number of informational
 * log messages.</p>
 *
 * <p>By declaring the base package for each module explicitly, each
 * Spring Data module scans only its own sub-package. This eliminates the
 * informational messages and makes the repository configuration
 * unambiguous.</p>
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.project.code.Repo.jpa")
@EnableMongoRepositories(basePackages = "com.project.code.Repo.mongo")
public class RepositoryConfig {
    // No additional configuration is required.
}
