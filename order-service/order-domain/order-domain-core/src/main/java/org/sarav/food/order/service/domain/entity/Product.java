package org.sarav.food.order.service.domain.entity;

import org.sarav.food.system.domain.entity.BaseEntity;
import org.sarav.food.system.domain.valueobjects.Money;
import org.sarav.food.system.domain.valueobjects.ProductId;

public class Product extends BaseEntity<ProductId> {

    private String name;
    private Money price;

    public Product(ProductId productId, String name, Money price) {
        this.setId(productId);
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public Money getPrice() {
        return price;
    }
}
