package org.sarav.food.order.system.app.exception;

import lombok.extern.slf4j.Slf4j;
import org.sarav.food.order.system.app.dto.ErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorDTO handleGlobalException(Exception e) {
        return ErrorDTO.builder().message("Unexpected Error Occurred!")
                .phrase(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .build();
    }

    @ExceptionHandler(value = {ValidationException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorDTO handleGlobalValidationException(ValidationException e) {
        if (e instanceof ConstraintViolationException) {
            String msg = extractViolationsFromException((ConstraintViolationException) e);
            log.error(msg, e);
            return ErrorDTO.builder().message(msg)
                    .phrase(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                    .build();
        } else {
            log.error(e.getMessage());
            return ErrorDTO.builder().message(e.getMessage())
                    .phrase(HttpStatus.BAD_REQUEST.getReasonPhrase())
                    .build();
        }


    }


    private String extractViolationsFromException(ConstraintViolationException exception) {

        return exception.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("--"));

    }

}
