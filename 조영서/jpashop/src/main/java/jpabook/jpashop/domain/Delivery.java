package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Delivery {

    @Id @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    private Order order;

    @Embedded
    private Address address;

    @Enumerated(EnumType.ORDINAL) //숫자로 들어가는
    private DeliveryStatus status; //READY, COMP
}
