package org.sarav.food.order.service.app;

import org.mockito.Mockito;
import org.sarav.food.order.service.app.ports.output.message.publisher.payment.PaymentRequestMessagePublisher;
import org.sarav.food.order.service.app.ports.output.message.publisher.restaurantapproval.RestaurantApprovalRequestMessagePublisher;
import org.sarav.food.order.service.app.ports.output.repository.*;
import org.sarav.food.order.service.domain.OrderDomainService;
import org.sarav.food.order.service.domain.OrderDomainServiceImpl;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = "org.sarav.food.order.service.app")
public class OrderTestConfiguration {
    @Bean
    public PaymentRequestMessagePublisher orderCreatedPaymentRequestMessagePublisher() {
        return Mockito.mock(PaymentRequestMessagePublisher.class);
    }

    @Bean
    public RestaurantApprovalRequestMessagePublisher restaurantApprovalRequestMessagePublisher() {
        return Mockito.mock(RestaurantApprovalRequestMessagePublisher.class);
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
    public PaymentOutboxRepository paymentOutboxRepository() {
        return Mockito.mock(PaymentOutboxRepository.class);
    }

    @Bean
    public RestaurantApprovalOutboxRepository restaurantApprovalOutboxRepository() {
        return Mockito.mock(RestaurantApprovalOutboxRepository.class);
    }

    @Bean
    public OrderDomainService orderDomainService() {
        return new OrderDomainServiceImpl();
    }

}
