package org.sarav.food.order.service.app;

import lombok.extern.slf4j.Slf4j;
import org.sarav.food.order.service.app.dto.message.CustomerModel;
import org.sarav.food.order.service.app.mapper.OrderDataMapper;
import org.sarav.food.order.service.app.ports.input.message.listener.customer.CustomerMessageListener;
import org.sarav.food.order.service.app.ports.output.repository.CustomerRepository;
import org.sarav.food.order.service.domain.entity.Customer;
import org.sarav.food.order.service.domain.exception.OrderDomainException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomerMessageListenerImpl implements CustomerMessageListener {

    private final CustomerRepository customerRespository;
    private final OrderDataMapper orderDataMapper;

    public CustomerMessageListenerImpl(CustomerRepository customerRespository, OrderDataMapper orderDataMapper) {
        this.customerRespository = customerRespository;
        this.orderDataMapper = orderDataMapper;
    }

    @Override
    public void customerCreated(CustomerModel customerModel) {
        log.info("Creating Customer {}", customerModel.getId());
        Customer customer = customerRespository.save(orderDataMapper.customerModelToCustomer(customerModel));
        if (customer == null) {
            log.error("Error Occurred and unable to save customer {}", customer.getId());
            throw new OrderDomainException("Error Occurred and unable to save customer " + customer.getId());
        }
        log.info("Customer: {} has been created successfully");
    }
}
