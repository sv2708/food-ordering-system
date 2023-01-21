package org.sarav.food.order.system.saga;

public interface SagaStep<T> {

    void success(T data);

    void rollback(T data);

}
