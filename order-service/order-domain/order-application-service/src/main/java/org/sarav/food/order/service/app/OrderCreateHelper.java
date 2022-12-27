package org.sarav.food.order.service.app;

import lombok.extern.slf4j.Slf4j;
import org.sarav.food.order.service.app.dto.create.CreateOrderCommand;
import org.sarav.food.order.service.app.mapper.OrderDataMapper;
import org.sarav.food.order.service.app.ports.output.repository.CustomerRepository;
import org.sarav.food.order.service.app.ports.output.repository.OrderRepository;
import org.sarav.food.order.service.app.ports.output.repository.RestaurantRepository;
import org.sarav.food.order.service.domain.OrderDomainService;
import org.sarav.food.order.service.domain.entity.Customer;
import org.sarav.food.order.service.domain.entity.Order;
import org.sarav.food.order.service.domain.entity.Restaurant;
import org.sarav.food.order.service.domain.event.OrderCreatedEvent;
import org.sarav.food.order.service.domain.exception.OrderDomainException;
import org.sarav.food.order.system.domain.event.publisher.DomainEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
public class OrderCreateHelper {

    private final OrderDomainService orderDomainService;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final RestaurantRepository restaurantRepository;
    private final OrderDataMapper orderDataMapper;
    private final DomainEventPublisher<OrderCreatedEvent> orderCreatedEventPublisher;

    public OrderCreateHelper(OrderDomainService orderDomainService, OrderRepository orderRepository, CustomerRepository customerRepository, RestaurantRepository restaurantRepository, OrderDataMapper orderDataMapper, DomainEventPublisher<OrderCreatedEvent> orderCreatedEventPublisher) {
        this.orderDomainService = orderDomainService;
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.restaurantRepository = restaurantRepository;
        this.orderDataMapper = orderDataMapper;
        this.orderCreatedEventPublisher = orderCreatedEventPublisher;
    }

    @Transactional
    public OrderCreatedEvent persistOrder(CreateOrderCommand createOrderCommand) {
        checkCustomerExists(createOrderCommand.getCustomerId());
        Restaurant restaurant = checkRestaurantExists(createOrderCommand);
        Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
        // application service calls the domain service for validation and creates an event
        OrderCreatedEvent orderCreatedEvent = orderDomainService.validateAndInitiateOrder(order, restaurant,
                orderCreatedEventPublisher);
        saveOrder(order);
        log.info("Order creation Successful");
        return orderCreatedEvent;
    }

    private Order saveOrder(Order order) {
        Order savedOrder = orderRepository.save(order);
        if (savedOrder == null) {
            log.error("Order Creation failed for order {}", order.getId());
            throw new OrderDomainException("Order Creation failed for Order " + order.getId());
        }
        log.info("Order Creation is Successful for Order {}", order.getId());
        return savedOrder;
    }


    private Restaurant checkRestaurantExists(CreateOrderCommand createOrderCommand) {
        Restaurant restaurant = orderDataMapper.createOrderCommandToRestaurant(createOrderCommand);
        Optional<Restaurant> restaurantOptional = restaurantRepository.findRestaurantInformation(restaurant);
//        Optional<Restaurant> restaurantOptional = restaurantRepository.findRestaurantById(restaurant.getId().getValue());
        if (restaurantOptional.isEmpty()) {
            log.warn("Restaurant with ID {} is not found.", restaurant.getId().getValue());
            throw new OrderDomainException("Restaurant with ID " + restaurant.getId().getValue() + " is not found");
        }
        return restaurantOptional.get();
    }

    private void checkCustomerExists(UUID customerId) {
        Optional<Customer> customer = customerRepository.findCustomer(customerId);
        if (customer.isEmpty()) {
            log.warn("Customer with id {} is not found", customerId);
            throw new OrderDomainException("customer with ID " + customerId + "is not found");
        }
    }


}
