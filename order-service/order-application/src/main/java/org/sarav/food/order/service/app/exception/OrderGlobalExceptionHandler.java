package org.sarav.food.order.service.app.exception;

import org.sarav.food.order.service.domain.exception.OrderDomainException;
import org.sarav.food.order.service.domain.exception.OrderNotFoundException;
import org.sarav.food.order.system.app.dto.ErrorDTO;
import org.sarav.food.order.system.app.exception.GlobalExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class OrderGlobalExceptionHandler extends GlobalExceptionHandler {

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {OrderDomainException.class})
    public ErrorDTO handleOrderDomainException(OrderDomainException e) {
        return ErrorDTO.builder().phrase(HttpStatus.BAD_REQUEST.getReasonPhrase()).message(e.getMessage()).build();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = {OrderNotFoundException.class})
    public ErrorDTO handleOrderNotFoundException(OrderNotFoundException e) {
        return ErrorDTO.builder().phrase(HttpStatus.NOT_FOUND.getReasonPhrase()).message(e.getMessage()).build();
    }

}
