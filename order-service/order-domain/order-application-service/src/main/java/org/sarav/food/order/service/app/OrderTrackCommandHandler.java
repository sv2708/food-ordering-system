package org.sarav.food.order.service.app;

import lombok.extern.slf4j.Slf4j;
import org.sarav.food.order.service.app.dto.track.TrackOrderQuery;
import org.sarav.food.order.service.app.dto.track.TrackOrderResponse;
import org.sarav.food.order.service.app.mapper.OrderDataMapper;
import org.sarav.food.order.service.app.ports.output.repository.OrderRepository;
import org.sarav.food.order.service.domain.entity.Order;
import org.sarav.food.order.service.domain.exception.OrderNotFoundException;
import org.sarav.food.order.service.domain.valueobjects.TrackingId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@Slf4j
public class OrderTrackCommandHandler {

    private final OrderDataMapper orderDataMapper;
    private final OrderRepository orderRepository;

    public OrderTrackCommandHandler(OrderDataMapper orderDataMapper, OrderRepository orderRepository) {
        this.orderDataMapper = orderDataMapper;
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public TrackOrderResponse trackOrder(TrackOrderQuery trackOrderQuery) {
        Optional<Order> orderResult = orderRepository.findByTrackingId(new TrackingId(trackOrderQuery.getTrackingOrderId()));
        if (orderResult.isEmpty()) {
            log.warn("Order not found for tracking order {}", trackOrderQuery.getTrackingOrderId());
            throw new OrderNotFoundException("Order Not found for tracking order " + trackOrderQuery.getTrackingOrderId());
        }
        TrackOrderResponse trackOrderResponse = orderDataMapper.convertOrderToTrackOrderResponse(orderResult.get());
        return trackOrderResponse;
    }

}
