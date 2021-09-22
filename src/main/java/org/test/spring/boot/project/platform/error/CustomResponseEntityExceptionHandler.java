package org.test.spring.boot.project.platform.error;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.test.spring.boot.project.platform.api.ValidationException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * Customized response entity exception handler
 */
@ControllerAdvice
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        ValidationException validationException = new ValidationException(ex.getBindingResult());
        return handleExceptionInternal(ex, validationException.getErrors(), headers, BAD_REQUEST, request);
    }
}