package org.sarav.food.order.service.dataaccess.customer.adapter;

import org.sarav.food.order.service.app.ports.output.repository.CustomerRepository;
import org.sarav.food.order.service.dataaccess.customer.mapper.CustomerEntityDataMapper;
import org.sarav.food.order.service.dataaccess.customer.repository.CustomerJpaRepository;
import org.sarav.food.order.service.domain.entity.Customer;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class CustomerRepositoryImpl implements CustomerRepository {

    private final CustomerJpaRepository customerJpaRepository;
    private final CustomerEntityDataMapper customerEntityDataMapper;

    public CustomerRepositoryImpl(CustomerJpaRepository customerJpaRepository, CustomerEntityDataMapper customerEntityDataMapper) {
        this.customerJpaRepository = customerJpaRepository;
        this.customerEntityDataMapper = customerEntityDataMapper;
    }


    @Override
    public Optional<Customer> findCustomer(UUID customerId) {
        return customerJpaRepository.findById(customerId).map(customerEntityDataMapper::customerEntityToCustomer);
    }

    @Override
    public Customer save(Customer customer) {
        return customerEntityDataMapper.customerEntityToCustomer(
                customerJpaRepository.save(customerEntityDataMapper.customerToCustomerEntity(customer)));
    }
     
}
