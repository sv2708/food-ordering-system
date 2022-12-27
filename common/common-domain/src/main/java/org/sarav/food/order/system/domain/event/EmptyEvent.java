package org.sarav.food.order.system.domain.event;

public final class EmptyEvent implements DomainEvent<Void> {

    private static final EmptyEvent INSTANCE = new EmptyEvent();

    private EmptyEvent() {
    }

    public static EmptyEvent getInstance() {
        return INSTANCE;
    }

    @Override
    public void fire() {

    }

}
