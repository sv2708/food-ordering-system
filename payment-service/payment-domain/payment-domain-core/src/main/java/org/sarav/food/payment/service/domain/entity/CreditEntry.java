package org.sarav.food.payment.service.domain.entity;

import org.sarav.food.order.system.domain.entity.AggregateRoot;
import org.sarav.food.order.system.domain.valueobjects.CustomerId;
import org.sarav.food.order.system.domain.valueobjects.Money;
import org.sarav.food.payment.service.domain.valueobjects.CreditEntryId;

import java.util.UUID;

public class CreditEntry extends AggregateRoot<CreditEntryId> {

    private final CustomerId customerId;
    private Money totalCreditAmount;

    private CreditEntry(Builder builder) {
        this.setId(builder.id);
        customerId = builder.customerId;
        totalCreditAmount = builder.totalCreditAmount;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public Money getTotalCreditAmount() {
        return totalCreditAmount;
    }

    public void initialize() {
        this.setId(new CreditEntryId(UUID.randomUUID()));
    }

    public void addCreditAmount(Money amount) {
        totalCreditAmount = totalCreditAmount.add(amount);
    }

    public void subtractCreditAmount(Money amount) {
        totalCreditAmount = totalCreditAmount.subtract(amount);
    }

    public static Builder builder() {
        return new Builder();
    }


    public static final class Builder {
        private CreditEntryId id;
        private CustomerId customerId;
        private Money totalCreditAmount;

        private Builder() {
        }

        public Builder id(CreditEntryId val) {
            id = val;
            return this;
        }

        public Builder customerId(CustomerId val) {
            customerId = val;
            return this;
        }

        public Builder totalCreditAmount(Money val) {
            totalCreditAmount = val;
            return this;
        }

        public CreditEntry build() {
            return new CreditEntry(this);
        }
    }
}

