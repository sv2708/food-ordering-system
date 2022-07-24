package org.sarav.food.order.service.domain.entity;

import org.sarav.food.order.service.domain.exception.OrderDomainException;
import org.sarav.food.order.service.domain.valueobjects.OrderItemId;
import org.sarav.food.order.service.domain.valueobjects.StreetAddress;
import org.sarav.food.order.service.domain.valueobjects.TrackingId;
import org.sarav.food.system.domain.entity.AggregateRoot;
import org.sarav.food.system.domain.valueobjects.*;

import java.util.List;
import java.util.UUID;

public class Order extends AggregateRoot<OrderId> {

    private CustomerId customerId;
    private RestaurantId restaurantId;
    private StreetAddress deliveryAddress;
    private Money price;
    private List<OrderItem> items;
    private TrackingId trackingId;
    private OrderStatus orderStatus;
    private List<String> failureMessages;

    private Order(Builder builder) {
        this.setId(builder.id);
        customerId = builder.customerId;
        restaurantId = builder.restaurantId;
        deliveryAddress = builder.deliveryAddress;
        price = builder.price;
        items = builder.items;
        trackingId = builder.trackingId;
        orderStatus = builder.orderStatus;
        failureMessages = builder.failureMessages;
    }

    public static Builder builder() {
        return new Builder();
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public RestaurantId getRestaurantId() {
        return restaurantId;
    }

    public StreetAddress getDeliveryAddress() {
        return deliveryAddress;
    }

    public Money getPrice() {
        return price;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public TrackingId getTrackingId() {
        return trackingId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public List<String> getFailureMessages() {
        return failureMessages;
    }

    public void initializeOrder(){
        setId(new OrderId(UUID.randomUUID()));
        trackingId = new TrackingId(UUID.randomUUID());
        orderStatus = OrderStatus.PENDING;
        initializeOrderItems();
    }

    /**
     * Validate order for its price and field values
     **/
    public void validateOrder(){
        validateInitialOrder();
        validateOrderPrice();
    }

    public void validateOrderPrice(){
        validateTotalPrice();
        validateItemsPrice();
    }

    private void validateInitialOrder(){
        if(orderStatus != null || getId() != null){
            throw new OrderDomainException("Invalid Order State for initialization");
        }
    }

    private void validateTotalPrice(){
        if(price == null || !price.isGreaterThanZero()){
            throw new OrderDomainException("Total Price must be greater than Zero");
        }
    }

    /**
     * Validate items price and total price values
     * sent from client
     */
    private void validateItemsPrice(){
        Money itemsTotal = this.getItems().stream().map(item -> item.getSubTotal()).reduce(Money.ZERO, Money::add);

        for(OrderItem item: this.getItems()){
            this.validateItemPrice(item);
        }

        if(!itemsTotal.equals(this.price)){
            throw new OrderDomainException("Order Total Price " + this.price.toString() + " is not equal to the items total price "+ itemsTotal.toString());
        }
    }

    /**
     * Validate individual order item price
     * @param item
     */
    private void validateItemPrice(OrderItem item){
        if(!item.isPriceValid()){
            throw new OrderDomainException("Invalid Order item price "+ item.getPrice().getAmount() + " is not equal to " + price.getAmount());
        }
    }

    private void initializeOrderItems() {
        long itemId = 1;
        for(OrderItem item: this.items){
            item.initializeOrderItem(new OrderItemId(itemId++), super.getId());
        }
    }

    public void pay(){
        if(this.orderStatus != OrderStatus.PENDING){
            throw new OrderDomainException("Order is not in right status to change to PAID state");
        }
        this.orderStatus = OrderStatus.PAID;
    }

    public void approve(){
        if(this.orderStatus != OrderStatus.PAID){
            throw new OrderDomainException("Order is not in right status to change to APPROVED state");
        }
        this.orderStatus = OrderStatus.APPROVED;
        updateFailureMessages(failureMessages);
    }

    public void initCancel(List<String> failureMessages){
        if(this.orderStatus != OrderStatus.PAID){
            throw new OrderDomainException("Order is not in right status to change to CANCELLING state");
        }
        this.orderStatus = OrderStatus.CANCELLING;
        updateFailureMessages(failureMessages);
    }

    private void updateFailureMessages(List<String> failureMessages) {

        if(this.failureMessages == null){
            this.failureMessages = failureMessages;
        }else{
            if(failureMessages != null && this.failureMessages != null){
                this.failureMessages.addAll(failureMessages.stream().filter(msg -> !msg.isEmpty()).toList());
            }
        }


    }

    public void cancel(List<String> failureMessages){
        if(this.orderStatus != OrderStatus.CANCELLED || this.orderStatus != OrderStatus.PENDING){
            throw new OrderDomainException("Order is not in right status to change to CANCELLING state");
        }
        this.orderStatus = OrderStatus.CANCELLED;
        this.updateFailureMessages(failureMessages);
    }

    public static final class Builder {
        private OrderId id;
        private CustomerId customerId;
        private RestaurantId restaurantId;
        private StreetAddress deliveryAddress;
        private Money price;
        private List<OrderItem> items;
        private TrackingId trackingId;
        private OrderStatus orderStatus;
        private List<String> failureMessages;

        private Builder() {
        }

        public Builder id(OrderId val) {
            id = val;
            return this;
        }

        public Builder customerId(CustomerId val) {
            customerId = val;
            return this;
        }

        public Builder restaurantId(RestaurantId val) {
            restaurantId = val;
            return this;
        }

        public Builder deliveryAddress(StreetAddress val) {
            deliveryAddress = val;
            return this;
        }

        public Builder price(Money val) {
            price = val;
            return this;
        }

        public Builder items(List<OrderItem> val) {
            items = val;
            return this;
        }

        public Builder trackingId(TrackingId val) {
            trackingId = val;
            return this;
        }

        public Builder orderStatus(OrderStatus val) {
            orderStatus = val;
            return this;
        }

        public Builder failureMessages(List<String> val) {
            failureMessages = val;
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }
}
