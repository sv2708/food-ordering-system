package org.sarav.food.restaurant.service.app.ports.output.repository;


import org.sarav.food.restaurant.service.domain.entity.Restaurant;

import java.util.Optional;

public interface RestaurantRepository {
    Optional<Restaurant> findRestaurantInformation(Restaurant restaurant);
}
