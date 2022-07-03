package org.sarav.food.order.service.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

public class StreetAddress {

    private final UUID id;
    private final String addressLine1;
    private final String addressLine2;
    private final String zipcode;
    private final String city;

    public StreetAddress(UUID id, String addressLine1, String addressLine2, String zipcode, String city) {
        this.id = id;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.zipcode = zipcode;
        this.city = city;
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
        if (!(o instanceof StreetAddress)) return false;
        StreetAddress that = (StreetAddress) o;
        return Objects.equals(addressLine1, that.addressLine1) && Objects.equals(addressLine2, that.addressLine2) && Objects.equals(zipcode, that.zipcode) && Objects.equals(city, that.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(addressLine1, addressLine2, zipcode, city);
    }
}
