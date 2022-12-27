package org.sarav.food.order.service.app;

import lombok.extern.slf4j.Slf4j;
import org.sarav.food.order.service.app.ports.output.repository.OrderRepository;
import org.sarav.food.order.service.domain.entity.Order;
import org.sarav.food.order.service.domain.exception.OrderNotFoundException;
import org.sarav.food.order.system.domain.valueobjects.OrderId;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class OrderSagaHelper {

    private final OrderRepository orderRepository;

    public OrderSagaHelper(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void saveOrder(Order order) {
        orderRepository.save(order);
    }

    public Order findOrder(String orderId) {
        Optional<Order> orderOptional = orderRepository.findById(new OrderId(UUID.fromString(orderId)));
        if (orderOptional.isEmpty()) {
            log.error("Order {} is not present");
            throw new OrderNotFoundException("Order " + orderId + " is not found");
        }
        return orderOptional.get();
    }

}
