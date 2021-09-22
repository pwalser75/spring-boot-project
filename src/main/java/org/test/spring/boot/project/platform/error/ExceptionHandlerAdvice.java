package org.test.spring.boot.project.platform.error;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.test.spring.boot.project.notes.api.ResourceNotFoundException;
import org.test.spring.boot.project.platform.api.ValidationException;

import javax.validation.ConstraintViolationException;
import java.util.NoSuchElementException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Exception handler advice.
 */
@ControllerAdvice
public class ExceptionHandlerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler({NoSuchElementException.class, ResourceNotFoundException.class})
    protected ResponseEntity<Object> handleNotFound(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = ex.getMessage();
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), NOT_FOUND, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<Object> handleBadRequest(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = ex.getMessage();
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), BAD_REQUEST, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleBadRequest(ConstraintViolationException ex, WebRequest request) {
        ValidationException validationException = new ValidationException(ex);
        return handleExceptionInternal(ex, validationException.getErrors(), new HttpHeaders(), BAD_REQUEST, request);
    }

    @ExceptionHandler(ValidationException.class)
    protected ResponseEntity<Object> handleValidationException(ValidationException ex, WebRequest request) {
        return handleExceptionInternal(ex, ex.getErrors(), new HttpHeaders(), BAD_REQUEST, request);
    }
}