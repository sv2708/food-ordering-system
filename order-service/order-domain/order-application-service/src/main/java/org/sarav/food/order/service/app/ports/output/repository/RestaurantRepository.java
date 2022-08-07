package org.sarav.food.order.service.app.ports.output.repository;

import org.sarav.food.order.service.domain.entity.Restaurant;

import java.util.Optional;
import java.util.UUID;

public interface RestaurantRepository {

    Optional<Restaurant> findRestaurantInformation(Restaurant restaurant);

    Optional<Restaurant> findRestaurantById(UUID restaurantId);

}
