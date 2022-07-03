package org.sarav.food.order.service.domain.valueobject;

import org.sarav.food.system.domain.valueobjects.BaseId;

public class OrderItemId extends BaseId<Long> {
    public OrderItemId(Long id) {
        super(id);
    }
}
