package org.sarav.food.order.service.app.ports.input;

import lombok.extern.slf4j.Slf4j;
import org.sarav.food.order.service.app.dto.track.TrackOrderQuery;
import org.sarav.food.order.service.app.dto.track.TrackOrderResponse;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderTrackCommandHandler {

    public TrackOrderResponse trackOrder(TrackOrderQuery trackOrderQuery){
        return null;
    }

}
