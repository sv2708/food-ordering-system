package org.sarav.food.order.system.domain.event;

public interface DomainEvent<T> {
    void fire();
}
