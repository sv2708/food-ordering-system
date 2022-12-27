package org.sarav.food.order.system.saga;

import org.sarav.food.order.system.domain.event.DomainEvent;

public interface SagaStep<T, S extends DomainEvent, F extends DomainEvent> {

    S success(T data);

    F rollback(T data);

}
