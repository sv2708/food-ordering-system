package org.sarav.food.order.system.domain.event.publisher;

import org.sarav.food.order.system.domain.event.DomainEvent;

public interface DomainEventPublisher<T extends DomainEvent> {

    void publish(T domainEvent);

}
