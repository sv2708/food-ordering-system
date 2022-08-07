package org.sarav.food.order.service.app;

import org.mockito.Mockito;
import org.sarav.food.order.service.app.ports.output.message.publisher.payment.OrderCancelledPaymentRequestMessagePublisher;
import org.sarav.food.order.service.app.ports.output.message.publisher.payment.OrderCreatedPaymentRequestMessagePublisher;
import org.sarav.food.order.service.app.ports.output.message.publisher.restaurantapproval.OrderPaidRestaurantRequestMessagePublisher;
import org.sarav.food.order.service.app.ports.output.repository.CustomerRepository;
import org.sarav.food.order.service.app.ports.output.repository.OrderRepository;
import org.sarav.food.order.service.app.ports.output.repository.RestaurantRepository;
import org.sarav.food.order.service.domain.OrderDomainService;
import org.sarav.food.order.service.domain.OrderDomainServiceImpl;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = "org.sarav.food.order.service.app")
public class OrderTestConfiguration {
    @Bean
    public OrderCreatedPaymentRequestMessagePublisher orderCreatedPaymentRequestMessagePublisher() {
        return Mockito.mock(OrderCreatedPaymentRequestMessagePublisher.class);
    }

    @Bean
    public OrderCancelledPaymentRequestMessagePublisher orderCancelledPaymentRequestMessagePublisher() {
        return Mockito.mock(OrderCancelledPaymentRequestMessagePublisher.class);
    }

    @Bean
    public OrderPaidRestaurantRequestMessagePublisher orderPaidRestaurantRequestMessagePublisher() {
        return Mockito.mock(OrderPaidRestaurantRequestMessagePublisher.class);
    }

    @Bean
    public OrderRepository orderRepository() {
        return Mockito.mock(OrderRepository.class);
    }

    @Bean
    public RestaurantRepository restaurantRepository() {
        return Mockito.mock(RestaurantRepository.class);
    }

    @Bean
    public CustomerRepository customerRepository() {
        return Mockito.mock(CustomerRepository.class);
    }

    @Bean
    public OrderDomainService orderDomainService() {
        return new OrderDomainServiceImpl();
    }

}
