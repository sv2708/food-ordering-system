package org.sarav.food.order.system.order.service.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

}
