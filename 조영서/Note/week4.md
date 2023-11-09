# 4주차 스터디

## ****주문, 주문상품 엔티티 개발****

- 구현 기능
    - 상품 주문
    - 주문 내역 조회
    - 주문 취소
- 순서
    1. 주문 엔티티, 주문 상품 엔티티 개발
    2. 주문 리포지토리 개발
    3. 주문 서비스 개발
    4. 주문 검색 기능 개발
    5. 주문 기능 테스트
- Order.java 수정

```
package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="orders")
@Getter @Setter
public class Order {
    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;//주문 상태, [ORDER, CANCEL]//==연관관계 메서드==//public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }
    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

//==생성 메서드==//public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems){
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem : orderItems){
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

//==비즈니스 로직==////주문취소public void cancel(){
        if (delivery.getStatus() == DeliveryStatus.COMP){
            throw new IllegalStateException("이미 배송 완료된 상품은 취소가 불간으합니다.");
        }
        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem : orderItems){
            orderItem.cancel();
        }
    }

//==조회 로직==////전체 주문 가격 조회public int getTotalPRice(){
        return orderItems.stream()
                .mapToInt(OrderItem::getTotalPrice).sum();

    }
}
```

- OrderItem.java 수정

```
package jpabook.jpashop.domain;

import jakarta.persistence.*;
import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "order_item")
public class OrderItem {

    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice;
    private int count;

//==생성 메서드==//public static OrderItem createOrderItem(Item item, int orderPrice, int
            count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);
        item.removeStock(count);
        return orderItem;
    }

//==조회 로직==////주문상품 전체 가격 조회public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }

//==비즈니스 로직==//public void cancel() {//다시 재고 수량 증가시키기
        getItem().addStock(count);
    }
}
```

- Order와 OrderItem에 대한 생성 메소드, 조회 로직, 비즈니스 로직을 구현하였다

## **주문 리포지토리 개발**

- jpashop/src/main/java/jpabook/jpashop/repository/OrderRepository.java 생성

```
package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final EntityManager em;//주입 받음public void save(Order order){
        em.persist(order);
    }

    public Order findOne(Long id){
        return em.find(Order.class, id);
    }

//    public List<Order> findAll(OrderSearch orderSearch){}
}
```

### **주문 서비스 개발**

- jpashop/src/main/java/jpabook/jpashop/service/OrderService.java 생성

```
package jpabook.jpashop.service;

import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
/** 주문 */@Transactional
    public Long order(Long memberId, Long itemId, int count) {
//엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);
//배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());
//delivery.setStatus(DeliveryStatus.READY);//주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);
//주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);
//주문 저장
        orderRepository.save(order);
        return order.getId();
    }
/** 주문 취소 */@Transactional
    public void cancelOrder(Long orderId) {//Id만 넘어옴//주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);
//주문 취소
        order.cancel();
    }
/** 주문 검색 *//*
 public List<Order> findOrders(OrderSearch orderSearch) {
 return orderRepository.findAll(orderSearch);
 }
*/
}
```

- 주문( order() ) : 주문하는 회원 식별자, 상품 식별자, 주문 수량 정보를 받아서 실제 주문 엔티티를 생성한 후 저장한다.
- 주문 취소( cancelOrder() ) : 주문 식별자를 받아서 주문 엔티티를 조회한 후 주문 엔티티에 주문 취소를 요청한다.
- 주문 검색( findOrders() ) : OrderSearch 라는 검색 조건을 가진 객체로 주문 엔티티를 검색한다.
- cascade 범위
    - 참조하는 것이 private일 때 사용하는 것이 좋다
    - 중요해서 여러 곳에서 참조하는 것에는 사용하면 안된다
- Order.java와 OrderItem.java에 아래 어노테이션을 추가한다. 직접 생성하는 것을 막기 위함이다.

```java
@NoArgsConstructor(access = AccessLevel.PROTECTED)
```

- JPA를 활용하면 엔티티 안에 있는 data를 바꾸면 알아서 변경된 포인트들을 찾아(dirty checking) DB에 업데이트 쿼리를 보낸다.
- 도메인 모델 패턴 : 엔티티에 핵심 비즈니스 로직을 다 넣는 형태, 서비스 계층이 단순히 엔티티에 필요한 요청을 위임하는 역할을 한다 (JPA,ORM)
- 트랜잭션 스크립트 패턴 : 엔티티에 비즈니스 로직이 없고 서비스 계층에서 비즈니스 로직을 처리하는 것 (SQL)
    - 둘 중 유지보수를 감안하여 고려해보면 된다.

## **주문 기능 테스트**

- jpashop/src/test/java/jpabook/jpashop/service/OrderServiceTest.java 생성

```
package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {
    @PersistenceContext EntityManager em;
    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;
    @Test
    public void 상품주문() throws Exception {
//Given
        Member member = createMember();
        Item item = createBook("시골 JPA", 10000, 10);//이름, 가격, 재고int orderCount = 2;
//When
        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);
//Then
        Order getOrder = orderRepository.findOne(orderId);
        assertEquals("상품 주문시 상태는 ORDER",OrderStatus.ORDER, getOrder.getStatus());
        assertEquals("주문한 상품 종류 수가 정확해야 한다.",1, getOrder.getOrderItems().size());
        assertEquals("주문 가격은 가격 * 수량이다.", 10000 * 2, getOrder.getTotalPrice());
        assertEquals("주문 수량만큼 재고가 줄어야 한다.",8, item.getStockQuantity());
    }
    @Test
    public void 상품주문_재고수량초과() throws Exception {
//Given
        Member member = createMember();
        Item item = createBook("시골 JPA", 10000, 10);//이름, 가격, 재고int orderCount = 11;//재고보다 많은 수량//When
        orderService.order(member.getId(), item.getId(), orderCount);
//Then
        fail("재고 수량 부족 예외가 발생해야 한다.");
    }
    @Test
    public void 주문취소() throws Exception {
//Given
        Member member = createMember();
        Item item = createBook("시골 JPA", 10000, 10);//이름, 가격, 재고int orderCount = 2;

        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);
//When
        orderService.cancelOrder(orderId);
//Then
        Order getOrder = orderRepository.findOne(orderId);
        assertEquals("주문 취소시 상태는 CANCEL 이다.",OrderStatus.CANCEL, getOrder.getStatus());
        assertEquals("주문이 취소된 상품은 그만큼 재고가 증가해야 한다.", 10, item.getStockQuantity());
    }
    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "강가", "123-123"));
        em.persist(member);
        return member;
    }
    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setStockQuantity(stockQuantity);
        book.setPrice(price);
        em.persist(book);
        return book;
    }
}
```

## **주문 검색 기능 개발**

- JPA에서 동적 쿼리를 해결하는 방법
- jpashop/src/main/java/jpabook/jpashop/repository/OrderSearch.java 생성

```
package jpabook.jpashop.repository;

import jpabook.jpashop.domain.OrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderSearch {
    private String memberName;//회원 이름private OrderStatus orderStatus;//주문 상태[ORDER, CANCEL]//Getter, Setter
}
```

- OrderRepository.java 수정
- JPQL로 처리하기
    - 조건에 따라 분기하여 JPQL을 조립해야 한다

```
@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final EntityManager em;//주입 받음public void save(Order order){
        em.persist(order);
    }

    public Order findOne(Long id){
        return em.find(Order.class, id);
    }

    public List<Order> findAllByString(OrderSearch orderSearch) {
//language=JPAQL
        String jpql = "select o From Order o join o.member m";
        boolean isFirstCondition = true;
//주문 상태 검색if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }
//회원 이름 검색if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }
        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000);//최대 1000건if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }
        return query.getResultList();
    }
```

- Criteria로 해결하기
    - 유지보수가 잘 안된다

```
    //JPA Criteria
    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Order, Member> m = o.join("member", JoinType.INNER); //회원과 조인
        List<Predicate> criteria = new ArrayList<>();
        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }
        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name = cb.like(m.<String>get("name"), "%" + orderSearch.getMemberName() + "%");
            criteria.add(name);
        }
        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000); //최대 1000건
        return query.getResultList();
    }

}
```

- QueryDSL로 해결하는 방법도 있다.
