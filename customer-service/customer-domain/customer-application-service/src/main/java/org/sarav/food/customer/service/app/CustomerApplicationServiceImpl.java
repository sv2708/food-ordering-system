package org.sarav.food.customer.service.app;

import lombok.extern.slf4j.Slf4j;
import org.sarav.food.customer.service.app.create.CreateCustomerCommand;
import org.sarav.food.customer.service.app.create.CreateCustomerResponse;
import org.sarav.food.customer.service.app.mapper.CustomerDataMapper;
import org.sarav.food.customer.service.app.ports.input.service.CustomerApplicationService;
import org.sarav.food.customer.service.app.ports.output.message.publisher.CustomerMessagePublisher;
import org.sarav.food.customer.service.domain.event.CustomerCreatedEvent;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Service
class CustomerApplicationServiceImpl implements CustomerApplicationService {

    private final CustomerCreateCommandHandler customerCreateCommandHandler;

    private final CustomerDataMapper customerDataMapper;

    private final CustomerMessagePublisher customerMessagePublisher;

    public CustomerApplicationServiceImpl(CustomerCreateCommandHandler customerCreateCommandHandler,
                                          CustomerDataMapper customerDataMapper,
                                          CustomerMessagePublisher customerMessagePublisher) {
        this.customerCreateCommandHandler = customerCreateCommandHandler;
        this.customerDataMapper = customerDataMapper;
        this.customerMessagePublisher = customerMessagePublisher;
    }

    @Override
    public CreateCustomerResponse createCustomer(CreateCustomerCommand createCustomerCommand) {
        CustomerCreatedEvent customerCreatedEvent = customerCreateCommandHandler.createCustomer(createCustomerCommand);
        customerMessagePublisher.publish(customerCreatedEvent);
        return customerDataMapper
                .customerToCreateCustomerResponse(customerCreatedEvent.getCustomer(),
                        "Customer saved successfully!");
    }
}
