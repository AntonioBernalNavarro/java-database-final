
package com.project.code.Controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for all REST controllers.
 *
 * <p>This class centralizes the handling of exceptions that may be thrown
 * by any controller in the application. By using the
 * {@code @RestControllerAdvice} annotation, Spring intercepts the
 * exceptions declared in the {@code @ExceptionHandler} methods and
 * produces a consistent JSON response for the client.</p>
 *
 * <p>The goal is to avoid leaking internal stack traces to the consumer
 * and to provide meaningful, structured error messages that the frontend
 * can process uniformly.</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles errors caused by malformed or unreadable request bodies.
     *
     * <p>This exception is typically raised by Spring when the incoming
     * JSON payload cannot be parsed into the expected Java object, for
     * example when the JSON syntax is invalid or when the structure does
     * not match the target DTO.</p>
     *
     * @param exception the exception instance raised by the framework
     * @return a map containing a single {@code message} key with a
     *         user-friendly error description
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleJsonParseException(HttpMessageNotReadableException exception) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Invalid input: The data provided is not valid.");
        return response;
    }
}
