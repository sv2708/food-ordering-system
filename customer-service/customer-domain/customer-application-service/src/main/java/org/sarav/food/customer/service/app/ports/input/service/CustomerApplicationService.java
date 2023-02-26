package org.sarav.food.customer.service.app.ports.input.service;


import org.sarav.food.customer.service.app.create.CreateCustomerCommand;
import org.sarav.food.customer.service.app.create.CreateCustomerResponse;

import javax.validation.Valid;

public interface CustomerApplicationService {

    CreateCustomerResponse createCustomer(@Valid CreateCustomerCommand createCustomerCommand);

}
