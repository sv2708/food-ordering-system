package org.sarav.food.order.service.domain.entity;

import org.sarav.food.order.service.domain.valueobjects.OrderItemId;
import org.sarav.food.system.domain.entity.BaseEntity;
import org.sarav.food.system.domain.valueobjects.Money;
import org.sarav.food.system.domain.valueobjects.OrderId;

import java.math.BigDecimal;

public class OrderItem extends BaseEntity<OrderItemId> {

     private OrderId orderId;
     private final Product product;
     private final int quantity;
     private final Money subTotal;
     private final Money price;

     private OrderItem(Builder builder) {
          this.setId(builder.orderItemId);
          orderId = builder.orderId;
          product = builder.product;
          quantity = builder.quantity;
          subTotal = builder.subTotal;
          price = builder.price;
     }

     public static Builder newBuilder() {
          return new Builder();
     }

     public OrderId getOrderId() {
          return orderId;
     }

     public Product getProduct() {
          return product;
     }

     public int getQuantity() {
          return quantity;
     }


     public Money getSubTotal() {
          return subTotal;
     }

     public Money getPrice() {
          return price;
     }

     public void initializeOrderItem(OrderItemId id, OrderId orderId) {
          this.setId(id);
          this.orderId = orderId;
     }

     public boolean isPriceValid(){
          Money calcPrice = this.getProduct().getPrice().multiply(new Money(new BigDecimal(this.getQuantity())));
          return this.getSubTotal().isGreaterThanZero() &&
                  price.equals(product.getPrice()) &&
                  calcPrice.equals(this.getSubTotal());
     }

     public static final class Builder {
          private OrderItemId orderItemId;
          private OrderId orderId;
          private Product product;
          private int quantity;
          private Money money;
          private Money subTotal;
          private Money price;

          private Builder() {
          }

          public Builder id(OrderItemId val) {
               orderItemId = val;
               return this;
          }

          public Builder orderId(OrderId val) {
               orderId = val;
               return this;
          }

          public Builder product(Product val) {
               product = val;
               return this;
          }

          public Builder quantity(int val) {
               quantity = val;
               return this;
          }

          public Builder money(Money val) {
               money = val;
               return this;
          }

          public Builder subTotal(Money val) {
               subTotal = val;
               return this;
          }

          public Builder price(Money val) {
               price = val;
               return this;
          }


          public OrderItem build() {
               return new OrderItem(this);
          }
     }
}
