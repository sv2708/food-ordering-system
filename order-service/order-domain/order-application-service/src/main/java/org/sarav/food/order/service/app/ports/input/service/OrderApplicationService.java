package org.sarav.food.order.service.app.ports.input.service;

import org.sarav.food.order.service.app.dto.create.CreateOrderCommand;
import org.sarav.food.order.service.app.dto.create.CreateOrderResponse;
import org.sarav.food.order.service.app.dto.track.TrackOrderQuery;
import org.sarav.food.order.service.app.dto.track.TrackOrderResponse;

import javax.validation.Valid;

public interface OrderApplicationService {

    CreateOrderResponse createOrder(@Valid CreateOrderCommand createOrderCommand);

    TrackOrderResponse trackOrder(@Valid TrackOrderQuery trackOrderQuery);

}
