package org.sarav.food.order.service.app.ports.output.repository;

import org.sarav.food.order.service.domain.entity.Restaurant;

import java.util.Optional;

public interface RestaurantRepository {

    Optional<Restaurant> findRestaurantInformation(Restaurant restaurant);

}
