package org.sarav.food.payment.service.app.ports.output.repository;

import org.sarav.food.order.system.domain.valueobjects.CustomerId;
import org.sarav.food.payment.service.domain.entity.CreditEntry;

import java.util.Optional;

public interface CreditEntryRepository {

    CreditEntry save(CreditEntry creditEntry);

    Optional<CreditEntry> findByCustomerId(CustomerId customerId);
}
