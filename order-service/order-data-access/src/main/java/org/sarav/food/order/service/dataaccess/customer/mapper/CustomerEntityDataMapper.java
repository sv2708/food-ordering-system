package org.sarav.food.order.service.dataaccess.customer.mapper;

import org.sarav.food.order.service.dataaccess.customer.entity.CustomerEntity;
import org.sarav.food.order.service.domain.entity.Customer;
import org.sarav.food.order.system.domain.valueobjects.CustomerId;
import org.springframework.stereotype.Component;

@Component
public class CustomerEntityDataMapper {

    public Customer customerEntityToCustomer(CustomerEntity entity) {
        return new Customer(new CustomerId(entity.getId()));
    }

}
