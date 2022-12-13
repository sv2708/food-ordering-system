package org.sarav.food.payment.service.app.ports.output.repository;

import org.sarav.food.payment.service.domain.entity.CreditEntry;

import java.util.Optional;
import java.util.UUID;

public interface CreditEntryRepository {

    void save(CreditEntry creditEntry);

    Optional<CreditEntry> findByCustomerId(UUID customerId);
}
