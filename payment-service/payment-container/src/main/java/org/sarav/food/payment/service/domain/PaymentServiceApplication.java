package org.sarav.food.payment.service.domain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "org.sarav.food.payment.service.dataaccess")
@EntityScan(basePackages = "org.sarav.food.payment.service.dataaccess")
@SpringBootApplication(scanBasePackages = "org.sarav")
public class PaymentServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }
}
