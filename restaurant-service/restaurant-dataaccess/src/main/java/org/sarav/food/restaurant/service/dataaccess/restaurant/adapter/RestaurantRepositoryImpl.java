package org.sarav.food.restaurant.service.dataaccess.restaurant.adapter;

import org.sarav.food.restaurant.service.app.ports.output.repository.RestaurantRepository;
import org.sarav.food.restaurant.service.dataaccess.restaurant.mapper.RestaurantDataAccessMapper;
import org.sarav.food.restaurant.service.domain.entity.Restaurant;
import org.sarav.food.service.dataaccess.entity.RestaurantEntity;
import org.sarav.food.service.dataaccess.repository.RestaurantJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class RestaurantRepositoryImpl implements RestaurantRepository {

    private final RestaurantJpaRepository restaurantJpaRepository;
    private final RestaurantDataAccessMapper restaurantDataAccessMapper;

    public RestaurantRepositoryImpl(RestaurantJpaRepository restaurantJpaRepository,
                                    RestaurantDataAccessMapper restaurantDataAccessMapper) {
        this.restaurantJpaRepository = restaurantJpaRepository;
        this.restaurantDataAccessMapper = restaurantDataAccessMapper;
    }

    @Override
    public Optional<Restaurant> findRestaurantInformation(Restaurant restaurant) {
        List<UUID> restaurantProducts =
                restaurantDataAccessMapper.restaurantToRestaurantProducts(restaurant);
        Optional<List<RestaurantEntity>> restaurantEntities = restaurantJpaRepository
                .findByRestaurantIdAndProductIdIn(restaurant.getId().getValue(),
                        restaurantProducts);
        return restaurantEntities.map(restaurantDataAccessMapper::restaurantEntityToRestaurant);
    }
}
