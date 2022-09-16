package org.sarav.food.order.service.dataaccess.order.adapter;

import org.sarav.food.order.service.app.ports.output.repository.OrderRepository;
import org.sarav.food.order.service.dataaccess.order.mapper.OrderEntityDataMapper;
import org.sarav.food.order.service.dataaccess.order.repository.OrderJpaRepository;
import org.sarav.food.order.service.domain.entity.Order;
import org.sarav.food.order.service.domain.valueobjects.TrackingId;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderEntityDataMapper orderEntityDataMapper;
    private final OrderJpaRepository orderJpaRepository;


    public OrderRepositoryImpl(OrderEntityDataMapper orderEntityDataMapper, OrderJpaRepository orderJpaRepository) {
        this.orderEntityDataMapper = orderEntityDataMapper;
        this.orderJpaRepository = orderJpaRepository;
    }

    @Override
    public Order save(Order order) {
        var savedOrderEntity = orderJpaRepository.save(orderEntityDataMapper.OrderToOrderEntity(order));
        return orderEntityDataMapper.orderEntityToOrder(savedOrderEntity);
    }

    @Override
    public Optional<Order> findByTrackingId(TrackingId trackingId) {
        return orderJpaRepository.findByTrackingId(trackingId.getValue())
                .map(orderEntityDataMapper::orderEntityToOrder);
    }
}
