# 주문 도메인 개발

## 주문, 주문상품 엔티티 개발
* 주문 엔티티에 직접 **주문을 생성하는 메서드**를 추가
    ```java
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
    ```
* 주문 엔티티에 **비즈니스 로직** 직접 추가
    ```java
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
    ```
## 주문 리포지토리 개발
* 주문 리포지토리 코드
    ```java
    package com.jpabook.jpashop.repository;

    import com.jpabook.jpashop.domain.Order;
    import jakarta.persistence.EntityManager;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Repository;

    import java.util.List;

    @Repository
    @RequiredArgsConstructor
    public class OrderRepository {
        private final EntityManager em;

        public void save(Order order){
            em.persist(order);
        }

        public Order findOne(Long id){
            return em.find(Order.class,id);
        }
        
    //    public List<Order> findAll(OrderSearch orderSearch){}
    }

    ```