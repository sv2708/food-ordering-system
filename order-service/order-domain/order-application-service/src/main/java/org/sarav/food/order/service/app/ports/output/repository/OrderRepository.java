package org.sarav.food.order.service.app.ports.output.repository;


import org.sarav.food.order.service.domain.entity.Order;
import org.sarav.food.order.service.domain.valueobjects.TrackingId;

import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findByTrackingId(TrackingId trackingId);

}
