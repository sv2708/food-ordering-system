package org.sarav.food.order.service.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.sarav.food.order.service.app.dto.create.CreateOrderCommand;
import org.sarav.food.order.service.app.dto.create.CreateOrderResponse;
import org.sarav.food.order.service.app.dto.create.OrderAddress;
import org.sarav.food.order.service.app.dto.create.OrderItemEntity;
import org.sarav.food.order.service.app.mapper.OrderDataMapper;
import org.sarav.food.order.service.app.ports.input.service.OrderApplicationService;
import org.sarav.food.order.service.app.ports.output.repository.CustomerRepository;
import org.sarav.food.order.service.app.ports.output.repository.OrderRepository;
import org.sarav.food.order.service.app.ports.output.repository.RestaurantRepository;
import org.sarav.food.order.service.domain.entity.Customer;
import org.sarav.food.order.service.domain.entity.Order;
import org.sarav.food.order.service.domain.entity.Product;
import org.sarav.food.order.service.domain.entity.Restaurant;
import org.sarav.food.order.service.domain.exception.OrderDomainException;
import org.sarav.food.order.system.domain.valueobjects.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = OrderTestConfiguration.class)
public class OrderApplicationServiceTest {

    @Autowired
    private OrderApplicationService orderApplicationService;

    @Autowired
    private OrderDataMapper orderDataMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    private CreateOrderCommand createOrderCommand;
    private CreateOrderCommand createOrderCommandWrongPrice;
    private CreateOrderCommand createOrderCommandWrongProductPrice;

    private final UUID CUSTOMER_ID = UUID.fromString("520d3a18-8e64-439e-a052-9b9bfca326ed");
    private final UUID RESTAURANT_ID = UUID.fromString("520d3a18-8e64-439e-a052-9b9bfca326ed");
    private final UUID ORDER_ID = UUID.fromString("520d3a18-8e64-439e-a052-9b9bfca326ed");
    private final UUID PRODUCT_ID = UUID.fromString("520d3a18-8e64-439e-a052-9b9bfca326ed");
    private final BigDecimal PRICE = new BigDecimal("200.0");

    @BeforeEach
    private void init() {
        createOrderCommand = CreateOrderCommand.builder().customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .price(PRICE)
                .address(OrderAddress.builder().addressLine1("Line 1").addressLine2("2").city("city").postalCode("423532").build())
                .order(List.of(OrderItemEntity.builder().productId(PRODUCT_ID)
                        .price(new BigDecimal("50.0")).quantity(4)
                        .subTotal(new BigDecimal("200.0")).build()))
                .build();

        createOrderCommandWrongPrice = CreateOrderCommand.builder().customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .price(new BigDecimal("150"))
                .address(OrderAddress.builder().addressLine1("Line 1").addressLine2("2").city("city").postalCode("423532").build())
                .order(List.of(OrderItemEntity.builder().productId(PRODUCT_ID)
                        .price(new BigDecimal("50.0")).quantity(4)
                        .subTotal(new BigDecimal("200.0")).build()))
                .build();

        createOrderCommandWrongProductPrice = CreateOrderCommand.builder().customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .price(PRICE)
                .address(OrderAddress.builder().addressLine1("Line 1").addressLine2("2").city("city").postalCode("423532").build())
                .order(List.of(OrderItemEntity.builder().productId(PRODUCT_ID)
                        .price(new BigDecimal("50.0")).quantity(4)
                        .subTotal(new BigDecimal("150.0")).build()))
                .build();

        Customer customer = new Customer();
        customer.setId(new CustomerId(CUSTOMER_ID));

        Restaurant restaurantFromCommand = orderDataMapper.createOrderCommandToRestaurant(createOrderCommand);
        Restaurant restaurantResponse = Restaurant.newBuilder().id(new RestaurantId(RESTAURANT_ID))
                .active(true)
                .productList(List.of(Product.newBuilder().id(new ProductId(PRODUCT_ID))
                        .price(new Money(new BigDecimal("50.00")))
                        .name("product-1")
                        .build()))
                .build();

        Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
        order.setId(new OrderId(ORDER_ID));

        when(restaurantRepository.findRestaurantInformation(restaurantFromCommand)).thenReturn(Optional.of(restaurantResponse));
//        when(restaurantRepository.findRestaurantById(restaurantFromCommand.getId().getValue())).thenReturn(Optional.of(restaurantResponse));
        when(customerRepository.findCustomer(createOrderCommand.getCustomerId())).thenReturn(Optional.of(customer));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

    }

    @Test // normal flow
    public void testCreateOrder() {
        CreateOrderResponse createOrderResponse = orderApplicationService.createOrder(createOrderCommand);
        assertEquals(createOrderResponse.getOrderStatus(), OrderStatus.PENDING);
        assertNotNull(createOrderResponse.getOrderTrackingId());
    }

    @Test //total price of all items validation
    public void testCreateOrderWrongPrice() {
        var exception = assertThrows(OrderDomainException.class, () -> orderApplicationService.createOrder(createOrderCommandWrongPrice));
        var expectedMessage = "Order Total Price 150 is not equal to the items total price 200.00";
        assertEquals(expectedMessage, exception.getMessage());
    }


    @Test //individual item price (quantity * product price) validation
    public void testCreateOrderWrongProductPrice() {
        var exception = assertThrows(OrderDomainException.class, () -> orderApplicationService.createOrder(createOrderCommandWrongProductPrice));
        var expected = "Invalid Order item price 50.0 is not equal to 200.0";
        assertEquals(expected, exception.getMessage());
    }

    @Test
    public void testCreateOrderWhenRestaurantInactive() {
        Restaurant restaurantResponseInactive = Restaurant.newBuilder().id(new RestaurantId(RESTAURANT_ID))
                .active(false)
                .productList(List.of(Product.newBuilder().id(new ProductId(PRODUCT_ID))
                        .price(new Money(new BigDecimal("90.00")))
                        .name("product-1")
                        .build()))
                .build();
        Restaurant restaurantFromCommand = orderDataMapper.createOrderCommandToRestaurant(createOrderCommand);
        when(restaurantRepository.findRestaurantInformation(restaurantFromCommand)).thenReturn(Optional.of(restaurantResponseInactive));
        var exception = assertThrows(OrderDomainException.class, () -> orderApplicationService.createOrder(createOrderCommand));
        var expectedMsg = "Restaurant with id 520d3a18-8e64-439e-a052-9b9bfca326ed is not currently active";
        assertEquals(expectedMsg, exception.getMessage());
    }

}
