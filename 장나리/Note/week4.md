# 섹션 6 주문 도메인 개발

## 1.  주문, 주문상품 엔티티 개발

**구현 기능**

- 상품 주문
- 주문 내역 조회
- 주문 취소

**순서**

- 주문 엔티티, 주문상품 엔티티 개발
- 주문 리포지토리 개발
- 주문 서비스 개발
- 주문 검색 기능 개발
- 주문 기능 테스트

**엔티티 개발**

- 별도의 생성 메서드가 있으면 좋음
    
    ```java
    //== 생성 메서드 ==//
        public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems){
            Order order = new Order();
            order.setMember(member);
            order.setDelivery(delivery);
            for(OrderItem orderItem : orderItems){
                order.addOrderItem(orderItem);
            }
            order.setStatus(OrderStatus.ORDER);
            order.setOrderDate(LocalDateTime.now());
            return order;
        }
    ```
    
    - 생성 변경이 쉬워짐
- 비즈니스 로직 - 주문 취소
    - 주문 취소 시 재고가 올라가야 함
    
    ```java
    // Order.java
    //==비즈니스 로직==//
        // 주문 취소
        public void cancel(){
            if(delivery.getStatus() == DeliveryStatus.COMP){
                throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
            }
            this.setStatus(OrderStatus.CANCEL);
            for(OrderItem orderItem : orderItems){
                orderItem.cancel();
            }
        }
    
    // OrderItem.java
    //==비즈니스 로직==//
        public void cancel() {
            getItem().addStock(count);
    
        }
    ```
    
    - 배송 완료된 상품은 취소 불가능
- 조회 로직
    - 계산이 필요할 때
    
    ```java
    public int getTotalPrice(){
            return orderItems.stream().mapToInt(OrderItem::getTotalPrice).sum();
    }
    ```
    

## 2.  주문 리포지토리 개발

- 회원명 검색
- 주문 상태 검색
- 검색 기능은 5강에서

## 3.  주문 서비스 개발

- OrderRepository, MemberRepository, ItemRepository 필요

```java
@Transactional
    public Long order(Long memberId, Long itemId, int count){
        // 엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        // 배송 정보 설정
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());
        delivery.setStatus(DeliveryStatus.READY);

        // 주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        // 주문 생성
        Order order = Order.createOrder(member,delivery,orderItem);

        // 주문 저장
        orderRepository.save(order); // Cascade 옵션 때문에 한번만 해줘도 됨.

        return order.getId();
    }
```

- **주문,** order()
    - 주문하는 회원 식별자, 상품 식별자, 주문 수량 정보를 받아서 실제 주문 엔티티를 생성한 후 저장
    - 어디까지 cascade? 참조하는게 주인이 private owner인 경우. OrderItem, Delivery 모두 Order에서만 참조해서 씀
- **주문 취소,** cancelOrder()
    - 주문 식별자를 받아서 주문 엔티티를 조회한 후 주문 엔티티에 주문 취소
    요청
- **주문 검색,** findOrders()
    - OrderSearch 라는 검색 조건을 가진 객체로 주문 엔티티 검색
- 생성 로직을 변경할 때 생성 로직에서 어떤 필드를 추가한다거나 로직을 더 넣는다거나 → 이를 막아야 함.
    - ex) new Order();
    - contructor를 만들때 protected 로 → `@NoArgsConstructor(access = AccessLevel.PROTECTED)`
    - Order.java, OrderItem.java
- **도메인 모델 패턴**
    - 엔티티가 비즈니스 로직을 가지고 객체 지 향의 특성을 적극 활용하는 것
    - 서비스 계층은 단순히 엔티티에 필요한 요청을 위임하는 역할
- 트랜잭션 스크립트 패턴
    - 엔티티에는 비즈니스 로직이 거의 없고 서비스 계층에서 대부분 의 비즈니스 로직을 처리하는 것

→ 한 프로젝트 안에서도 도메인 모델, 트랜잭션 스트립트 패턴이 둘 다 있을 수 있음

## 4.  주문 기능 테스트

**테스트 요구사항**

- 상품 주문이 성공해야 한다.
    - Given : 테스트를 위한 회원과 상품 만듦
    - When : 실제 상품 주문
    - Then : 상품 주문 시 상태, 상품 종류 수, 주문 가격, 주문 후 재고 수량 검증
- 상품을 주문할 때 재고 수량을 초과하면 안 된다.
    - 재고 수량을 초과해서 상품을 주문하면 NotEnoughStockException 예외가 발생
- 주문 취소가 성공해야 한다.
    - 주문을 취소하면 그만큼 재고가 증가

좋은 테스트 

- DB에 dependency 없이, spring도 엮지 않고 순수하게 그 메소드를 단위 테스트하는 것이 좋음

## 5.  주문 검색 기능 개발

- 동적쿼리
    
    ```sql
    em.createQuery(" select o from Order o join o.member m " +
                    " where o.status = :status " +
                    " and m.name like :name ", Order.class)
                    .setParameter("status",orderSearch.getOrderStatus())
                    .setParameter("name",orderSearch.getMemberName())
                    .setMaxResults(1000) // 최대 1000건
                    .getResultList();
    ```
    
    1. jpql로 처리 → 너무 복잡 !!!!!!, 실무에서 쓰지 않음
        
        ```sql
                         String jpql = "select o From Order o join o.member m";
                boolean isFirstCondition = true;
        
                //주문 상태 검색
                if (orderSearch.getOrderStatus() != null) { // 값이 있으면 
                    if (isFirstCondition) { // 첫번째 조건
                        jpql += " where"; 
                        isFirstCondition = false;
                    } else {
                        jpql += " and"; // 그 다음 조건들은 and로 연결
                    }
                    jpql += " o.status = :status"; // jpql 동적으로 빌드
                }
        
                //회원 이름 검색
                if (StringUtils.hasText(orderSearch.getMemberName())) { // hasText : spring에서 제공하는 유틸리티
                    if (isFirstCondition) {
                        jpql += " where";
                        isFirstCondition = false;
                    } else {
                        jpql += " and";
                    }
                    jpql += " m.name like :name";
                }
        
                TypedQuery<Order> query = em.createQuery(jpql, Order.class) .setMaxResults(1000); //최대 1000건
        
                if (orderSearch.getOrderStatus() != null) {
                    query = query.setParameter("status", orderSearch.getOrderStatus());
                }
        
                if (StringUtils.hasText(orderSearch.getMemberName())) {
                    query = query.setParameter("name", orderSearch.getMemberName());
                }
        
                return query.getResultList();
        ```
        
        JPQL 쿼리를 문자로 생성하기는 번거롭고, 실수로 인한 버그가 충분히 발생할 수 있다.
        
    2. JPA Criteria로 처리 → 실무에서 쓰지 않음
        
        ```sql
                    CriteriaBuilder cb = em.getCriteriaBuilder();
                CriteriaQuery<Order> cq = cb.createQuery(Order.class);
                Root<Order> o = cq.from(Order.class);
                Join<Order, Member> m = o.join("member", JoinType.INNER); //회원과 조인
        
                List<Predicate> criteria = new ArrayList<>();
        
                //주문 상태 검색
                if (orderSearch.getOrderStatus() != null) {
                    Predicate status = cb.equal(o.get("status"),
                            orderSearch.getOrderStatus());
                    criteria.add(status);
                }
        
                //회원 이름 검색
                if (StringUtils.hasText(orderSearch.getMemberName())) {
                    Predicate name =
                            cb.like(m.<String>get("name"), "%" +
                                    orderSearch.getMemberName() + "%");
                    criteria.add(name);
                }
        
                cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
                TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000); //최대 1000건
                return query.getResultList();
        ```
        
        유지보수성 거의 제로 → 코드를 보고 무슨 쿼리가 만들어지는지 떠오르지 않음
        
    3. Querydsl → 해결책 !!

**스프링 부트 + JPA + 스프링 데이터 JPA + Querydsl**
