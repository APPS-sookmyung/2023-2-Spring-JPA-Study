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
## 주문 서비스 개발 
* 주문 저장할때 orderRepository.save만 하는 이유 
    ```java
        //주문 저장
        orderRepository.save(order);
    ```
    * Order 엔티티에서 `CascadeType all` 옵션 때문
        * OrderItem, Delivery가 자동적으로 repository에 persist 된다
        * 어디까지 cascadetype all 을 하는게 좋을까?
            * 라이프사이클에서 동일하게 관리할때 의미가 있는 것
            * Order, Delivery, OrderItem 사이의 관계
            * 다른 것이 참조할 수 없는 private owner일때만
            * 설핏 잘못하면 cascade 옵션때문에 한번에 다 지워질 수 있으므로 주의 
* `생성 로직`을 수정할 일이 생길때 아래와 같은 코드는 좋지 않음. 
    ```java
    OrderItem orderItem1=new OrderItem();
    ```
    * 따라서 OrderItem에 `protected`를 통해 위와 같은 코드를 작성하는 것을 막을 수 있음 -> 좋은 설계/유지 보수가 가능해진다 
        ```java
            protected OrderItem() {
        }
        ```
        * 이 코드도 **lombok**으로 줄일 수 있음! 
            ```java
            @NoArgsConstructor(access=AccesSLevel.PROTECTED)
            ```
* JPA의 장점: JPA를 활용하면 엔티티의 데이터가 바뀌면 JPA가 알아서 바뀐 변경 포인트를 (`더티 체킹`/`변경 내역 감지`) 체크하여 데이터베이스의 UPDATE 쿼리들을 날려줌 
* **`도메인 모델 패턴`**: 엔티티에 핵심 비즈니스 로직을 몰아넣는 스타일 
    * 서비스 계층은 단순히 엔티티에 필요한 요청을 위임하는 역할을 함 
    * 엔티티가 비즈니스 로직을 가지고 객체 지향의 특성을 적극 활용함 
* **`트랜잭션 스크립트 패턴`**: 반대로 엔티티에는 비즈니스 로직이 거의 없고 서비스 계층에서 대부분의 비즈니스 로직을 처리하는 것 