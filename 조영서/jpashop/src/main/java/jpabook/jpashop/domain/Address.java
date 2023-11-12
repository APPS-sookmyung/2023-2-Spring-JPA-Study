package jpabook.jpashop.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
public class Address {

    private String city;
    private String street;
    private String zipcode;

<<<<<<< Updated upstream
=======
    protected Address() {
    }

>>>>>>> Stashed changes
    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
