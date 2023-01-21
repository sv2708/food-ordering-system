package org.sarav.food.order.system.outbox;

public enum OutboxStatus {
    STARTED,
    COMPLETED,
    FAILED // failed due to kafka issue or network issue
}
