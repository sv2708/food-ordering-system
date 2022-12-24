package org.sarav.food.service.dataaccess.repository;

import org.sarav.food.service.dataaccess.entity.RestaurantEntity;
import org.sarav.food.service.dataaccess.entity.RestaurantEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RestaurantJpaRepository extends JpaRepository<RestaurantEntity, RestaurantEntityId> {

    Optional<List<RestaurantEntity>> findByRestaurantIdAndProductIdIn(UUID restaurantEntityId, List<UUID> productIds);

}
