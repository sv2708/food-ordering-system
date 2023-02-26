package org.sarav.food.customer.service.app.ports.output.message.publisher;

import org.sarav.food.customer.service.domain.event.CustomerCreatedEvent;

public interface CustomerMessagePublisher {

    void publish(CustomerCreatedEvent customerCreatedEvent);

}