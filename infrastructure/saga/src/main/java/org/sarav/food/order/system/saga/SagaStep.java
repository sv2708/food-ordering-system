package org.sarav.food.order.system.saga;

public interface SagaStep<T> {

    void success(T response);

    void rollback(T response);

}
