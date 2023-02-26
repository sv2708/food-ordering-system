package org.sarav.food.customer.service.app.rest;


import lombok.extern.slf4j.Slf4j;
import org.sarav.food.customer.service.app.create.CreateCustomerCommand;
import org.sarav.food.customer.service.app.create.CreateCustomerResponse;
import org.sarav.food.customer.service.app.ports.input.service.CustomerApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/customers", produces = "application/vnd.api.v1+json")
public class CustomerController {

    private final CustomerApplicationService customerApplicationService;

    public CustomerController(CustomerApplicationService customerApplicationService) {
        this.customerApplicationService = customerApplicationService;
    }

    @PostMapping(value = "create")
    public ResponseEntity<CreateCustomerResponse> createCustomer(@RequestBody CreateCustomerCommand
                                                                         createCustomerCommand) {
        log.info("Creating customer with username: {}", createCustomerCommand.getUsername());
        CreateCustomerResponse response = customerApplicationService.createCustomer(createCustomerCommand);
        return ResponseEntity.ok(response);
    }

}
