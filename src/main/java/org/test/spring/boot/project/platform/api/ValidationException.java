package org.test.spring.boot.project.platform.api;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.LinkedList;
import java.util.List;

import static java.util.stream.Collectors.joining;

/**
 * Validation exception.
 */
public class ValidationException extends RuntimeException {

    private final List<ValidationError> errors = new LinkedList<>();

    public ValidationException(ConstraintViolationException ex) {
        for (ConstraintViolation<?> error : ex.getConstraintViolations()) {
            errors.add(new ValidationError(error));
        }
    }

    public ValidationException(BindingResult bindingResult) {
        for (ObjectError error : bindingResult.getAllErrors()) {
            errors.add(new ValidationError(error));
        }
    }

    public ValidationException(List<ValidationError> validationErrors) {
        for (ValidationError error : validationErrors) {
            errors.add(error);
        }
    }

    public List<ValidationError> getErrors() {
        return errors;
    }

    @Override
    public String getMessage() {
        return errors.stream().map(ValidationError::toString).collect(joining(", "));
    }
}