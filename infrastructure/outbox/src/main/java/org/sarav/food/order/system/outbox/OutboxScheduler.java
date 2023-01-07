package org.sarav.food.order.system.outbox;

public interface OutboxScheduler {

    void processOutboxMessage();

}
