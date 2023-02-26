package org.sarav.food.order.service.app.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@AllArgsConstructor
public class CustomerModel {

    private final String id;
    private final String username;
    private final String firstName;
    private final String lastName;
}
