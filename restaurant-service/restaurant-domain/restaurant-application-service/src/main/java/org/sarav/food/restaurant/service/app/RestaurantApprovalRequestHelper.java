package org.sarav.food.restaurant.service.app;

import lombok.extern.slf4j.Slf4j;
import org.sarav.food.order.system.domain.valueobjects.OrderId;
import org.sarav.food.order.system.outbox.OutboxStatus;
import org.sarav.food.restaurant.service.app.dto.RestaurantApprovalRequest;
import org.sarav.food.restaurant.service.app.mapper.RestaurantDataMapper;
import org.sarav.food.restaurant.service.app.outbox.model.OrderOutboxMessage;
import org.sarav.food.restaurant.service.app.outbox.scheduler.OrderOutboxHelper;
import org.sarav.food.restaurant.service.app.ports.output.message.publisher.RestaurantApprovalResponseMessagePublisher;
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
    private final OrderOutboxHelper orderOutboxHelper;
    private final RestaurantApprovalResponseMessagePublisher responseMessagePublisher;

    public RestaurantApprovalRequestHelper(RestaurantDomainService restaurantDomainService,
                                           RestaurantDataMapper restaurantDataMapper,
                                           RestaurantRepository restaurantRepository,
                                           OrderApprovalRepository orderApprovalRepository,
                                           OrderOutboxHelper orderOutboxHelper,
                                           RestaurantApprovalResponseMessagePublisher responseMessagePublisher) {
        this.restaurantDomainService = restaurantDomainService;
        this.restaurantDataMapper = restaurantDataMapper;
        this.restaurantRepository = restaurantRepository;
        this.orderApprovalRepository = orderApprovalRepository;
        this.orderOutboxHelper = orderOutboxHelper;
        this.responseMessagePublisher = responseMessagePublisher;
    }

    @Transactional
    public void persistOrderApproval(RestaurantApprovalRequest restaurantApprovalRequest) {
        log.info("Processing restaurant approval for order id: {}", restaurantApprovalRequest.getOrderId());

        if (publishIfOutboxMessageProcessed(restaurantApprovalRequest)) {
            log.info("An outbox message with saga id: {} already saved to database!",
                    restaurantApprovalRequest.getSagaId());
            return;
        }

        List<String> failureMessages = new ArrayList<>();
        Restaurant restaurant = findRestaurant(restaurantApprovalRequest);

        OrderApprovalEvent orderApprovalEvent = restaurantDomainService.validateOrder(
                restaurant,
                failureMessages);
        orderApprovalRepository.save(restaurant.getOrderApproval());


        orderOutboxHelper
                .saveOrderOutboxMessage(restaurantDataMapper.orderApprovalEventToOrderEventPayload(orderApprovalEvent),
                        orderApprovalEvent.getOrderApproval().getApprovalStatus(),
                        OutboxStatus.STARTED,
                        UUID.fromString(restaurantApprovalRequest.getSagaId()));

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

    private boolean publishIfOutboxMessageProcessed(RestaurantApprovalRequest restaurantApprovalRequest) {
        Optional<OrderOutboxMessage> orderOutboxMessage =
                orderOutboxHelper.getCompletedOrderOutboxMessageBySagaIdAndOutboxStatus(UUID
                        .fromString(restaurantApprovalRequest.getSagaId()), OutboxStatus.COMPLETED);
        if (orderOutboxMessage.isPresent()) {
            responseMessagePublisher.publish(orderOutboxMessage.get(),
                    orderOutboxHelper::updateOutboxStatus);
            return true;
        }
        return false;
    }


}
