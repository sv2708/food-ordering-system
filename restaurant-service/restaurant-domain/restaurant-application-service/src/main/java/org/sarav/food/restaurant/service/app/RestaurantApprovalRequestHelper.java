package org.sarav.food.restaurant.service.app;

import lombok.extern.slf4j.Slf4j;
import org.sarav.food.order.system.domain.valueobjects.OrderId;
import org.sarav.food.restaurant.service.app.dto.RestaurantApprovalRequest;
import org.sarav.food.restaurant.service.app.mapper.RestaurantDataMapper;
import org.sarav.food.restaurant.service.app.ports.output.message.publisher.OrderApprovedMessagePublisher;
import org.sarav.food.restaurant.service.app.ports.output.message.publisher.OrderRejectedMessagePublisher;
import org.sarav.food.restaurant.service.app.ports.output.repository.OrderApprovalRepository;
import org.sarav.food.restaurant.service.app.ports.output.repository.RestaurantRepository;
import org.sarav.food.restaurant.service.domain.RestaurantDomainService;
import org.sarav.food.restaurant.service.domain.entity.Restaurant;
import org.sarav.food.restaurant.service.domain.event.OrderApprovalEvent;
import org.sarav.food.restaurant.service.domain.exception.RestaurantDomainException;
import org.sarav.food.restaurant.service.domain.exception.RestaurantNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RestaurantApprovalRequestHelper {

    private final RestaurantDomainService restaurantDomainService;
    private final RestaurantDataMapper restaurantDataMapper;
    private final RestaurantRepository restaurantRepository;
    private final OrderApprovalRepository orderApprovalRepository;
    private final OrderApprovedMessagePublisher orderApprovedMessagePublisher;
    private final OrderRejectedMessagePublisher orderRejectedMessagePublisher;

    public RestaurantApprovalRequestHelper(RestaurantDomainService restaurantDomainService,
                                           RestaurantDataMapper restaurantDataMapper,
                                           RestaurantRepository restaurantRepository,
                                           OrderApprovalRepository orderApprovalRepository,
                                           OrderApprovedMessagePublisher orderApprovedMessagePublisher,
                                           OrderRejectedMessagePublisher orderRejectedMessagePublisher) {
        this.restaurantDomainService = restaurantDomainService;
        this.restaurantDataMapper = restaurantDataMapper;
        this.restaurantRepository = restaurantRepository;
        this.orderApprovalRepository = orderApprovalRepository;
        this.orderApprovedMessagePublisher = orderApprovedMessagePublisher;
        this.orderRejectedMessagePublisher = orderRejectedMessagePublisher;
    }

    @Transactional
    public OrderApprovalEvent persistOrderApproval(RestaurantApprovalRequest restaurantApprovalRequest) {
        log.info("Processing restaurant approval for order id: {}", restaurantApprovalRequest.getOrderId());
        List<String> failureMessages = new ArrayList<>();
        Restaurant restaurant = findRestaurant(restaurantApprovalRequest);
        OrderApprovalEvent orderApprovalEvent =
                restaurantDomainService.validateOrder(
                        restaurant,
                        failureMessages,
                        orderApprovedMessagePublisher,
                        orderRejectedMessagePublisher);
        orderApprovalRepository.save(restaurant.getOrderApproval());
        return orderApprovalEvent;
    }

    private Restaurant findRestaurant(RestaurantApprovalRequest restaurantApprovalRequest) {
        Restaurant restaurant = restaurantDataMapper
                .restaurantApprovalRequestToRestaurant(restaurantApprovalRequest);
        Optional<Restaurant> restaurantResult = restaurantRepository.findRestaurantInformation(restaurant);
        if (restaurantResult.isEmpty()) {
            log.error("Restaurant with id " + restaurant.getId().getValue() + " not found!");
            throw new RestaurantNotFoundException("Restaurant with id " + restaurant.getId().getValue() +
                    " not found!");
        }

        Restaurant restaurantEntity = restaurantResult.get();
        restaurant.setActive(restaurantEntity.isActive());
        restaurant.getOrderDetail().getProducts().forEach(product ->
                restaurantEntity.getOrderDetail().getProducts().forEach(p -> {
                    if (p.getId().equals(product.getId())) {
                        product.updateWithConfirmedNamePriceAndAvailability(p.getName(), p.getPrice(), p.isAvailable());
                    }
                }));
        restaurant.getOrderDetail().setId(new OrderId(UUID.fromString(restaurantApprovalRequest.getOrderId())));

        Set<UUID> restaurantEntityProductIds = restaurantEntity.getOrderDetail().getProducts()
                .stream().map(product -> product.getId().getValue()).collect(Collectors.toSet());

        if (restaurant.getOrderDetail().getProducts().size() != restaurantEntityProductIds.size()) {
            throw new RestaurantDomainException("Not all Products in the Order " + restaurant.getOrderDetail().getId().getValue()
                    + " are available in the Restaurant " + restaurant.getId().getValue());
        }
        return restaurant;
    }
}
