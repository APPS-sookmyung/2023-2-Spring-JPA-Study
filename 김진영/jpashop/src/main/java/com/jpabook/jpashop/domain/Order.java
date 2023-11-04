package com.jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="orders") //테이블명이 관례대로 order가 될 수 있어서 지정함
@Getter @Setter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class Order {
    @Id @GeneratedValue
    @Column(name="order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id") //연관관계 매핑
    private Member member;

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name="delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate; //주문 시간

    @Enumerated(EnumType.STRING) //ENUM은 해당 어노테이션 필요
    private OrderStatus status; //주문 상태 [ORDER,CANCEL]

    //연관관계 메서드
    public void setMember(Member member){
        this.member=member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery){
        this.delivery=delivery;
        delivery.setOrder(this);
    }

    //생성 메서드
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems){
        Order order=new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for(OrderItem orderItem: orderItems){
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER); //처음 상태를 ORDER로 강제해둠
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    //엔티티에 직접 비즈니스 로직 추가
    /**
     * 주문 취소
     */
    public void cancel(){
        if (delivery.getStatus() ==DeliveryStatus.COMP){
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }

        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem: orderItems){
            orderItem.cancel(); //주문한 orderitem 각각에게 다 cancel을 날려줌
        }
    }

    //조회 로직

    /**
     * 전체 주문 가격 조회
     */
    public int getTotalPrice(){
//        int totalPrice=0;
//        for (OrderItem orderItem: orderItems){
//            totalPrice+=orderItem.getTotalPrice();
//        }
//        return totalPrice;

        //람다 사용하여 더 깔끔하게 코드 작성가능함
        return orderItems.stream()
                .mapToInt(OrderItem::getTotalPrice)
                .sum();
    }
}
