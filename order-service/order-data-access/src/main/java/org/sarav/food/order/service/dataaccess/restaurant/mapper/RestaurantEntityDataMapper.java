package org.sarav.food.order.service.dataaccess.restaurant.mapper;

import org.sarav.food.order.service.dataaccess.restaurant.entity.RestaurantEntity;
import org.sarav.food.order.service.dataaccess.restaurant.exception.RestaurantDataAccessException;
import org.sarav.food.order.service.domain.entity.Product;
import org.sarav.food.order.service.domain.entity.Restaurant;
import org.sarav.food.order.system.domain.valueobjects.Money;
import org.sarav.food.order.system.domain.valueobjects.ProductId;
import org.sarav.food.order.system.domain.valueobjects.RestaurantId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RestaurantEntityDataMapper {

    public List<UUID> restaurantToRestaurantProductIds(Restaurant restaurant) {

        return restaurant.getProductList().stream()
                .map(product -> product.getId().getValue())
                .collect(Collectors.toList());

    }

    /**
     * Returns a single restaurant from list of order_restaurant entity with same restaurantId but different productIds
     *
     * @param restaurantEntities
     * @return
     */
    public Restaurant restaurantEntitiesToRestaurant(List<RestaurantEntity> restaurantEntities) {
        RestaurantEntity restaurantEntity = restaurantEntities.stream().findFirst()
                .orElseThrow(() -> new RestaurantDataAccessException("Restaurant Not Found"));

        List<Product> restaurantProducts = restaurantEntities.stream()
                .map(entity -> Product.builder()
                        .id(new ProductId(entity.getProductId()))
                        .name(entity.getProductName())
                        .price(new Money(entity.getProductPrice()))
                        .build())
                .collect(Collectors.toList());

        return Restaurant.builder().id(new RestaurantId(restaurantEntity.getRestaurantId()))
                .active(restaurantEntity.getRestaurantActive())
                .productList(restaurantProducts)
                .build();
    }

}
