package org.sarav.food.customer.service.domain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan(basePackages = {"org.sarav.food.customer", "org.sarav.food.customer.service.dataaccess"})
@EnableJpaRepositories(basePackages = {"org.sarav.food.customer", "org.sarav.food.customer"})
@SpringBootApplication(scanBasePackages = "org.sarav.food")
public class CustomerServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }
}
