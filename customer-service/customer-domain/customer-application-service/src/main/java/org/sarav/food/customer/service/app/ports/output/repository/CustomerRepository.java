package org.sarav.food.customer.service.app.ports.output.repository;


import org.sarav.food.customer.service.domain.entity.Customer;

public interface CustomerRepository {

    Customer createCustomer(Customer customer);
}
