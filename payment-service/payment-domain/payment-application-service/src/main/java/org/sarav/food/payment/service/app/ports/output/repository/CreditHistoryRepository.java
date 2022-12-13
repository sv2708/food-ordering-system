package org.sarav.food.payment.service.app.ports.output.repository;

import org.sarav.food.payment.service.domain.entity.CreditHistory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CreditHistoryRepository {

    void save(CreditHistory creditHistory);

    Optional<List<CreditHistory>> findByCustomerId(UUID customerId);

}
