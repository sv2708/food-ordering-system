package org.sarav.food.order.service.dataaccess.restaurant.adapter;

import org.sarav.food.order.service.app.ports.output.repository.RestaurantRepository;
import org.sarav.food.order.service.dataaccess.restaurant.mapper.RestaurantEntityDataMapper;
import org.sarav.food.order.service.domain.entity.Restaurant;
import org.sarav.food.service.dataaccess.entity.RestaurantEntity;
import org.sarav.food.service.dataaccess.repository.RestaurantJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class RestaurantRepositoryImpl implements RestaurantRepository {

    private final RestaurantJpaRepository restaurantJpaRepository;
    private final RestaurantEntityDataMapper restaurantEntityDataMapper;

    public RestaurantRepositoryImpl(RestaurantJpaRepository restaurantJpaRepository, RestaurantEntityDataMapper restaurantEntityDataMapper) {
        this.restaurantJpaRepository = restaurantJpaRepository;
        this.restaurantEntityDataMapper = restaurantEntityDataMapper;
    }

    /**
     * @param restaurant -> Restaurant Domain Object with only list of available Product Ids
     * @return -> restaurant Domain Object with list of products info(price, name)
     */
    @Override
    public Optional<Restaurant> findRestaurantInformation(Restaurant restaurant) {

        List<UUID> restaurantProductIds = restaurantEntityDataMapper.restaurantToRestaurantProductIds(restaurant);
        Optional<List<RestaurantEntity>> restaurantEntities = restaurantJpaRepository.findByRestaurantIdAndProductIdIn(restaurant.getId().getValue(), restaurantProductIds);
        return restaurantEntities.map(restaurantEntityDataMapper::restaurantEntitiesToRestaurant);

    }

}
