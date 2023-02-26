package org.sarav.food.customer.service.domain.exception;


import org.sarav.food.order.system.domain.exception.DomainException;

public class CustomerDomainException extends DomainException {

    public CustomerDomainException(String message) {
        super(message);
    }
}
