package org.sarav.food.order.service.dataaccess.order.repository;

import org.sarav.food.order.service.dataaccess.order.entity.OrderEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderJpaRepository extends CrudRepository<OrderEntity, UUID> {
    Optional<OrderEntity> findByTrackingId(UUID uuid);
}
