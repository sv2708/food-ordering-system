package org.sarav.food.system.domain.valueobjects;

public abstract class BaseId<T> {

    private T id;

    protected BaseId(T id) {
        this.id = id;
    }
}
