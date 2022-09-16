package org.sarav.food.order.service.domain.valueobjects;

import java.util.Objects;
import java.util.UUID;

public class DeliveryAddress {

    private final UUID id;
    private final String addressLine1;
    private final String addressLine2;
    private final String zipcode;
    private final String city;

    public DeliveryAddress(UUID id, String addressLine1, String addressLine2, String zipcode, String city) {
        this.id = id;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.zipcode = zipcode;
        this.city = city;
    }

    private DeliveryAddress(Builder builder) {
        id = builder.id;
        addressLine1 = builder.addressLine1;
        addressLine2 = builder.addressLine2;
        zipcode = builder.zipcode;
        city = builder.city;
    }

    public static Builder newBuilder() {
        return new Builder();
    }


    public UUID getId() {
        return id;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public String getZipcode() {
        return zipcode;
    }

    public String getCity() {
        return city;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeliveryAddress)) return false;
        DeliveryAddress that = (DeliveryAddress) o;
        return Objects.equals(addressLine1, that.addressLine1) && Objects.equals(addressLine2, that.addressLine2) && Objects.equals(zipcode, that.zipcode) && Objects.equals(city, that.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(addressLine1, addressLine2, zipcode, city);
    }

    public static final class Builder {
        private UUID id;
        private String addressLine1;
        private String addressLine2;
        private String zipcode;
        private String city;

        private Builder() {
        }

        public Builder id(UUID val) {
            id = val;
            return this;
        }

        public Builder addressLine1(String val) {
            addressLine1 = val;
            return this;
        }

        public Builder addressLine2(String val) {
            addressLine2 = val;
            return this;
        }

        public Builder zipcode(String val) {
            zipcode = val;
            return this;
        }

        public Builder city(String val) {
            city = val;
            return this;
        }

        public DeliveryAddress build() {
            return new DeliveryAddress(this);
        }
    }
}
