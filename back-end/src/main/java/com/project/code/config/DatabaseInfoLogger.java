package com.project.code.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class that logs the active database host at application startup.
 * This class is intended to provide immediate visibility into which environment
 * (remote server or local machine) the application is currently connected to.
 */
@Configuration
public class DatabaseInfoLogger {

    private static final Logger log = LoggerFactory.getLogger(DatabaseInfoLogger.class);

    @Value("${db.host}")
    private String dbHost;

    /**
     * Registers a CommandLineRunner bean that executes immediately after the
     * application context has been fully initialized. It prints the active
     * database host and the target databases to the console.
     *
     * @return a CommandLineRunner instance that performs the logging operation
     */
    @Bean
    public CommandLineRunner logDatabaseInfo() {
        return args -> {
            log.info("================================================");
            log.info("  Database connection configuration");
            log.info("     Active host: {}", dbHost);
            log.info("     MySQL:       {}:3306/store_management", dbHost);
            log.info("     MongoDB:     {}:27017/store_reviews", dbHost);
            log.info("================================================");
        };
    }
}
