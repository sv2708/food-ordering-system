package org.sarav.food.order.service.app.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.sarav.food.order.system.domain.valueobjects.OrderApprovalStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class RestaurantApprovalResponse {

    private String id;
    private String sagaId;
    private UUID orderId;
    private String restaurantId;
    private Instant createdAt;
    private OrderApprovalStatus orderApprovalStatus;
    private List<String> failureMessages;

}
