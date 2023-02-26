package org.sarav.food.order.service.dataaccess.customer.mapper;

import org.sarav.food.order.service.dataaccess.customer.entity.CustomerEntity;
import org.sarav.food.order.service.domain.entity.Customer;
import org.sarav.food.order.system.domain.valueobjects.CustomerId;
import org.springframework.stereotype.Component;

@Component
public class CustomerEntityDataMapper {

    public Customer customerEntityToCustomer(CustomerEntity customerEntity) {
        return new Customer(new CustomerId(customerEntity.getId()), customerEntity.getUsername(),
                customerEntity.getFirstname(), customerEntity.getLastname());
    }

    public CustomerEntity customerToCustomerEntity(Customer customer) {
        return CustomerEntity.builder()
                .id(customer.getId().getValue())
                .username(customer.getUsername())
                .firstname(customer.getFirstName())
                .lastname(customer.getLastName())
                .build();
    }

}
