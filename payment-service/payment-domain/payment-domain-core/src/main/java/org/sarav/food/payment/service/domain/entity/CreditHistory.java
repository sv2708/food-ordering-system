package org.sarav.food.payment.service.domain.entity;

import org.sarav.food.order.system.domain.entity.AggregateRoot;
import org.sarav.food.order.system.domain.valueobjects.CustomerId;
import org.sarav.food.order.system.domain.valueobjects.Money;
import org.sarav.food.payment.service.domain.valueobjects.CreditHistoryId;

public class CreditHistory extends AggregateRoot<CreditHistoryId> {

    private Money amount;
    private final TransactionType transactionType;
    private final CustomerId customerId;

    private CreditHistory(Builder builder) {
        this.setId(builder.id);
        amount = builder.amount;
        transactionType = builder.transactionType;
        customerId = builder.customerId;
    }

    public static Builder builder() {
        return new Builder();
    }


    public static final class Builder {
        private CreditHistoryId id;
        private Money amount;
        private TransactionType transactionType;
        private CustomerId customerId;

        private Builder() {
        }

        public Builder id(CreditHistoryId val) {
            id = val;
            return this;
        }

        public Builder amount(Money val) {
            amount = val;
            return this;
        }

        public Builder transactionType(TransactionType val) {
            transactionType = val;
            return this;
        }

        public Builder customerId(CustomerId val) {
            customerId = val;
            return this;
        }

        public CreditHistory build() {
            return new CreditHistory(this);
        }
    }

    public Money getAmount() {
        return amount;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }
}
