package org.sarav.food.order.service.app.ports.output.repository;

import org.sarav.food.order.service.domain.entity.Customer;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {

    Optional<Customer> findCustomer(UUID customerId);

    Customer save(Customer customer);

}
