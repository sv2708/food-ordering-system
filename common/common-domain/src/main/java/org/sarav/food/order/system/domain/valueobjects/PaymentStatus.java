package org.sarav.food.order.system.domain.valueobjects;

public enum PaymentStatus {
    COMPLETED, // Payment Completed and Order sent to Restaurant for approval. OrderStatus will be PAID. SAGA Status=PROCESSING
    PENDING, // This will be the status when payment event is first consumed by payment service.
    FAILED, // Payment failed for some reasons like insufficient credit or payment issue.
    CANCELLED, // payment has been cancelled later(refund) as order is cancelled at the restaurant level.
}
