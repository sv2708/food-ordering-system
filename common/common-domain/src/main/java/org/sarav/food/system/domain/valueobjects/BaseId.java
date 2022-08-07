package org.sarav.food.system.domain.valueobjects;

import java.util.Objects;

public abstract class BaseId<T> {

    private T id;

    protected BaseId(T id) {
        this.id = id;
    }

    public T getValue() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseId)) return false;
        BaseId<?> baseId = (BaseId<?>) o;
        return Objects.equals(id, baseId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
