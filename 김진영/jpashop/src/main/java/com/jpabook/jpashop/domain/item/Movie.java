package com.jpabook.jpashop.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("M") //구분할때 쓰이는 값 (한테이블 안에 다 때려넣으니까)
@Getter @Setter
public class Movie extends Item{
    private String director;
    private String actor;
}
