package org.sarav.food.order.system.domain.valueobjects;

public enum OrderStatus {

    PENDING,
    SHIPPED,
    CANCELLED,
    CANCELLING,
    /**
     * CANCELLING -> when order service publishes order cancelled event,
     * it needs to be processed by payment service before setting CANCELLED
     */
    PAID,
    APPROVED

}
