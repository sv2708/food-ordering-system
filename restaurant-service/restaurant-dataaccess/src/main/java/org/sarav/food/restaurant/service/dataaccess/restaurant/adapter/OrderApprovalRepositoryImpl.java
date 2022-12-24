package org.sarav.food.restaurant.service.dataaccess.restaurant.adapter;

import org.sarav.food.restaurant.service.app.ports.output.repository.OrderApprovalRepository;
import org.sarav.food.restaurant.service.dataaccess.restaurant.mapper.RestaurantDataAccessMapper;
import org.sarav.food.restaurant.service.dataaccess.restaurant.repository.OrderApprovalJpaRepository;
import org.sarav.food.restaurant.service.domain.entity.OrderApproval;
import org.springframework.stereotype.Component;

@Component
public class OrderApprovalRepositoryImpl implements OrderApprovalRepository {

    private final OrderApprovalJpaRepository orderApprovalJpaRepository;
    private final RestaurantDataAccessMapper restaurantDataAccessMapper;

    public OrderApprovalRepositoryImpl(OrderApprovalJpaRepository orderApprovalJpaRepository,
                                       RestaurantDataAccessMapper restaurantDataAccessMapper) {
        this.orderApprovalJpaRepository = orderApprovalJpaRepository;
        this.restaurantDataAccessMapper = restaurantDataAccessMapper;
    }

    @Override
    public OrderApproval save(OrderApproval orderApproval) {
        return restaurantDataAccessMapper
                .orderApprovalEntityToOrderApproval(orderApprovalJpaRepository
                        .save(restaurantDataAccessMapper.orderApprovalToOrderApprovalEntity(orderApproval)));
    }

}
