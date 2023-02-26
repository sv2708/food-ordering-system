package org.sarav.food.customer.service.app.mapper;


import org.sarav.food.customer.service.app.create.CreateCustomerCommand;
import org.sarav.food.customer.service.app.create.CreateCustomerResponse;
import org.sarav.food.customer.service.domain.entity.Customer;
import org.sarav.food.order.system.domain.valueobjects.CustomerId;
import org.springframework.stereotype.Component;

@Component
public class CustomerDataMapper {

    public Customer createCustomerCommandToCustomer(CreateCustomerCommand createCustomerCommand) {
        return new Customer(new CustomerId(createCustomerCommand.getCustomerId()),
                createCustomerCommand.getUsername(),
                createCustomerCommand.getFirstName(),
                createCustomerCommand.getLastName());
    }

    public CreateCustomerResponse customerToCreateCustomerResponse(Customer customer, String message) {
        return new CreateCustomerResponse(customer.getId().getValue(), message);
    }
}
