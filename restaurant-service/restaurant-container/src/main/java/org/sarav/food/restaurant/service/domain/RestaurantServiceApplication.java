package org.sarav.food.restaurant.service.domain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = {"org.sarav.food.restaurant.service.dataaccess",
        "org.sarav.food.service.dataaccess"})
@EntityScan(basePackages = {"org.sarav.food.restaurant.service.dataaccess",
        "org.sarav.food.service.dataaccess"})
@SpringBootApplication(scanBasePackages = "org.sarav")
public class RestaurantServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RestaurantServiceApplication.class, args);
    }
}
