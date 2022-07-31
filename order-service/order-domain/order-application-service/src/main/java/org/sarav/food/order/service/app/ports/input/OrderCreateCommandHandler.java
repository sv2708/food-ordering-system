package org.sarav.food.order.service.app.ports.input;

import lombok.extern.slf4j.Slf4j;
import org.sarav.food.order.service.app.dto.create.CreateOrderCommand;
import org.sarav.food.order.service.app.dto.create.CreateOrderResponse;
import org.sarav.food.order.service.app.mapper.OrderDataMapper;
import org.sarav.food.order.service.app.ports.output.repository.CustomerRepository;
import org.sarav.food.order.service.app.ports.output.repository.OrderRepository;
import org.sarav.food.order.service.app.ports.output.repository.RestaurantRepository;
import org.sarav.food.order.service.domain.OrderDomainService;
import org.sarav.food.order.service.domain.entity.Customer;
import org.sarav.food.order.service.domain.entity.Order;
import org.sarav.food.order.service.domain.entity.Restaurant;
import org.sarav.food.order.service.domain.exception.OrderDomainException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
public class OrderCreateCommandHandler {

    private final OrderDomainService orderDomainService;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final RestaurantRepository restaurantRepository;
    private final OrderDataMapper orderDataMapper;

    public OrderCreateCommandHandler(OrderDomainService orderDomainService, OrderRepository orderRepository, CustomerRepository customerRepository, RestaurantRepository restaurantRepository, OrderDataMapper orderDataMapper) {
        this.orderDomainService = orderDomainService;
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.restaurantRepository = restaurantRepository;
        this.orderDataMapper = orderDataMapper;
    }

    @Transactional
    public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand){
        checkCustomerExists(createOrderCommand.getCustomerId());
        checkRestaurantExists(createOrderCommand);
        Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
        order.validateOrder();
        String response = createOrder(order);
        return orderDataMapper.convertOrderToCreateOrderResponse(order, response);
    }

    private String createOrder(Order order){
        Order savedOrder = orderRepository.save(order);
        if(savedOrder == null){
            log.error("Order Creation failed for order {}" , order.getId());
            throw new OrderDomainException("Order Creation failed for Order "+ order.getId());
        }
        log.info("Order Creation is Successful for Order {}", order.getId());
        return "Order Creation is Successful for Order " + order.getId();
    }

    private Restaurant checkRestaurantExists(CreateOrderCommand createOrderCommand) {
        Restaurant restaurant = orderDataMapper.createOrderCommandToRestaurant(createOrderCommand);
        Optional<Restaurant> restaurantOptional = restaurantRepository.findRestaurantInformation(restaurant);
        if(restaurantOptional.isEmpty()){
            log.warn("Restaurant with ID {} is not found.", restaurant.getId());
            throw new OrderDomainException("Restaurant with ID " + restaurant.getId() + "is not found");
        }
        return restaurantOptional.get();
    }

    private void checkCustomerExists(UUID customerId) {
        Optional<Customer> customer = customerRepository.findCustomer(customerId);
        if(customer.isEmpty()){
            log.warn("Customer with id {} is not found", customerId);
            throw new OrderDomainException("customer with ID "+ customerId + "is not found");
        }
    }

}
