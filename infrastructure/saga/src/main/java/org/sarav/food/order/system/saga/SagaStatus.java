package org.sarav.food.order.system.saga;

public enum SagaStatus {
    STARTED, FAILED, SUCCEEDED,
    PROCESSING, COMPENSATING, COMPENSATED
}
