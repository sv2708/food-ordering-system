package org.sarav.food.payment.service.domain;

import org.sarav.food.order.service.domain.OrderDomainService;
import org.sarav.food.order.service.domain.OrderDomainServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public OrderDomainService orderDomainService() {
        return new OrderDomainServiceImpl();
    }

}
