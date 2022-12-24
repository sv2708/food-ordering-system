package org.sarav.food.restaurant.service.app.ports.output.repository;

import org.sarav.food.restaurant.service.domain.entity.OrderApproval;

public interface OrderApprovalRepository {
    OrderApproval save(OrderApproval orderApproval);
}
