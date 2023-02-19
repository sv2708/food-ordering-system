package org.sarav.food.order.service.app.rest;

import lombok.extern.slf4j.Slf4j;
import org.sarav.food.order.service.app.dto.create.CreateOrderCommand;
import org.sarav.food.order.service.app.dto.create.CreateOrderResponse;
import org.sarav.food.order.service.app.dto.track.TrackOrderQuery;
import org.sarav.food.order.service.app.dto.track.TrackOrderResponse;
import org.sarav.food.order.service.app.ports.input.service.OrderApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/orders", produces = "application/vnd.api.v1+json")
public class OrderController {

    private final OrderApplicationService orderApplicationService;

    public OrderController(OrderApplicationService orderApplicationService) {
        this.orderApplicationService = orderApplicationService;
    }

    @PostMapping(value = "create")
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestBody CreateOrderCommand createOrderCommand) {
        log.info("Creating Order for {} in restaurant {}", createOrderCommand.getCustomerId(), createOrderCommand.getRestaurantId());
        CreateOrderResponse createOrderResponse = orderApplicationService.createOrder(createOrderCommand);
        log.info("Order has been created with tracking Id {}", createOrderResponse.getOrderTrackingId());
        return ResponseEntity.ok(createOrderResponse);
    }

    @GetMapping(value = "{trackingId}")
    public ResponseEntity<TrackOrderResponse> trackOrder(@PathVariable UUID trackingId) {
        log.info("Tracking the order with trackingId {} ", trackingId);
        TrackOrderQuery trackOrderQuery = TrackOrderQuery.builder().trackingOrderId(trackingId).build();
        TrackOrderResponse trackOrderResponse = orderApplicationService.trackOrder(trackOrderQuery);
        log.info("Returning the Order Status {} for tracking Id {}", trackOrderResponse.getOrderStatus(), trackOrderResponse.getOrderTrackingId());
        return ResponseEntity.ok(trackOrderResponse);
    }
}
