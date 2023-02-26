package org.sarav.food.customer.service.messaging.mapper;

import org.sarav.food.customer.service.domain.event.CustomerCreatedEvent;
import org.sarav.food.order.CustomerAvroModel;
import org.springframework.stereotype.Component;

@Component
public class CustomerMessagingDataMapper {

    public CustomerAvroModel paymentResponseAvroModelToPaymentResponse(CustomerCreatedEvent
                                                                               customerCreatedEvent) {
        return CustomerAvroModel.newBuilder()
                .setId(customerCreatedEvent.getCustomer().getId().getValue().toString())
                .setUsername(customerCreatedEvent.getCustomer().getUsername())
                .setFirstname(customerCreatedEvent.getCustomer().getFirstName())
                .setLastname(customerCreatedEvent.getCustomer().getLastName())
                .build();
    }
}
