package com.jpabook.jpashop.domain;

import com.jpabook.jpashop.domain.item.Item;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @Generated
    @Column(name="order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="order_id")
    private Order order;

    private int orderPrice; //주문 가격

    private int count; //주문 수량


    //생성 메서드
    public static OrderItem createOrderItem(Item item,int orderPrice, int count){
        OrderItem orderItem=new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStock(count);
        return orderItem;
    }

    //OrderItem에 직접 비즈니스 로직 추가
    public void cancel(){
        getItem().addStock(count); //주문을 취소하면 재고 상태를 다시 원상태로 복귀
    }

    //조회 로직
    /**
     *주문상품 전체 가격 조회
     */
    public int getTotalPrice() {
        return getOrderPrice()*count;
    }
}
