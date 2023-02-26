package org.sarav.food.order.service.app.ports.input.message.listener.customer;

import org.sarav.food.order.service.app.dto.message.CustomerModel;

public interface CustomerMessageListener {

    void customerCreated(CustomerModel customerModel);

}
