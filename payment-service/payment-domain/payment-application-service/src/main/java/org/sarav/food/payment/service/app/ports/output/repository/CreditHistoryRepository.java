package org.sarav.food.payment.service.app.ports.output.repository;

import org.sarav.food.order.system.domain.valueobjects.CustomerId;
import org.sarav.food.payment.service.domain.entity.CreditHistory;

import java.util.List;
import java.util.Optional;

public interface CreditHistoryRepository {

    CreditHistory save(CreditHistory creditHistory);

    Optional<List<CreditHistory>> findByCustomerId(CustomerId customerId);

}
