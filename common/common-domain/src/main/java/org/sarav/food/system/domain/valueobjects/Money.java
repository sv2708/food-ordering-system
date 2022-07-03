package org.sarav.food.system.domain.valueobjects;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class Money {
    private BigDecimal amount;

    public static final Money ZERO = new Money(BigDecimal.ZERO);

    public Money(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public boolean isGreaterThanZero(){
        return this.amount != null && this.amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isGreaterThan(Money amount){
        return this.amount != null && this.amount.compareTo(amount.getAmount()) > 0;
    }

    public Money add(Money money){
        return setScale(this.amount.add(money.getAmount()));
    }

    public Money subtract(Money money){
        return setScale(this.amount.subtract(money.getAmount()));
    }

    public Money multiply(Money money){
        return setScale(this.amount.multiply(money.getAmount()));
    }

    private Money setScale(BigDecimal amount){
         this.amount = amount.setScale(2, RoundingMode.HALF_EVEN);
         return this;
    }

    @Override
    public String toString() {
        return "" + amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money)) return false;
        Money money = (Money) o;
        return Objects.equals(amount, money.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }
}
