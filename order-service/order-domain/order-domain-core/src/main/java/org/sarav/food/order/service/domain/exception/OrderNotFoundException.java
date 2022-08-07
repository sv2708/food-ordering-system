package org.sarav.food.order.service.domain.exception;

import org.sarav.food.system.domain.exception.DomainException;

public class OrderNotFoundException extends DomainException {
    public OrderNotFoundException(String message) {
        super(message);
    }

    public OrderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
