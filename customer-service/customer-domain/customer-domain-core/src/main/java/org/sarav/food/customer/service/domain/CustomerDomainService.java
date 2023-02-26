package org.sarav.food.customer.service.domain;


import org.sarav.food.customer.service.domain.entity.Customer;
import org.sarav.food.customer.service.domain.event.CustomerCreatedEvent;

public interface CustomerDomainService {

    CustomerCreatedEvent validateAndInitiateCustomer(Customer customer);

}
