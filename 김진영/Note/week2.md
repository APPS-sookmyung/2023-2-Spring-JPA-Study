# 도메인 분석 설계 

## 도메인 모델과 테이블 설계 
* 주문과 상품은 다대다 관계 
    * 주문상품이라는 엔티티를 추가하여 다대다 관계를 일대다, 다대일 관계로 풀어냄 
* `다대다 관계`는 **사용하면 안됨** -> **일대다 관계로 풀어내야함** 
    * 가급적 단방향으로 사용하자. 양방향은 실무에서 자제. 
    * order을 생성할때 member가 필요 (member에서 order을 하는 것이 x) 라고 생각하자 
* 관계형 데이터베이스에서는 다대다관계를 일대다관계로 풀어야함 -> 중간에 entity를 만들어두어 풀어내야! 
* 일대다 관계에서는 '다'에 `외래키`가 존재함 -> 연관관계의 주인이 됨 
    * 외래키가 있는 주문을 연관관계의 주인으로 정하자 
        * Order.member를 ORDERS.MEMBER_ID 외래키와 매핑 
    * 연관관계의 주인은 단순히 외래 키를 누가 관리하냐의 문제이지 비즈니스상 우위에 있다고 주인으로 정하면 안됨 
        * 관리와 유지보수가 어려울 수 있음 
        * 추가적으로 별도의 업데이트 쿼리가 발생하는 성능 문제 발생 가능 

## 엔티티 클래스 개발 
* 실무에서는 가급적 *Getter는 열어두고* **Setter는 닫아두자**
    * 실무에서 엔티티의 데이터는 조회할 일이 너무 많아서 Getter의 경우 열어두는 것이 편리
    * Setter는 호출하면 데이터가 변함 -> 엔티티에 변경하는 이유를 추적하기 어려워짐   
        * Setter 대신에 변경 지점이 명확하도록 변경을 위한 비즈니스 메서드를 별도로 제공해야 함 
* 실무에서는 @ManyToMany 사용하지 말자 -> 운영 어려워짐 
* @Setter을 추가하지 않으면 **값 타입은 변경이 불가능함**
    * Address 
    * 임베디드 타입은 자바 기본 생성자를 public 또는 protected로 설정해야함 
* orderItems에 데이터를 저장해두고 order을 저장하면 한번에 persist됨 (원래 각각 entity에서 persist 해야하는데 order에서 한꺼번에 persist하는 것)
    ```java
        @OneToMany(mappedBy = "order",cascade = CascadeType.ALL)
        private List<OrderItem> orderItems = new ArrayList<>();
    ```
* 연관관계 메서드 : 양방향일 때 양쪽 세팅을 원자적으로 한 코드로 해결 가능 
    ```java
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
    ```

## 엔티티 설계 시 주의점 
1. 가급적 Setter을 사용하지 말자 
    * Setter가 모두 열려있으면 변경포인트가 너무 많아서 유지보수가 어려움 
2. **모든 연관관계는 지연로딩으로 설정** (외워)
    * 즉시로딩(`EAGER`)은 (Order을 조회할때 Member까지 모두 조회하는 것)은 예측이 어려움. 어떤 sql이 실행될지 추적이 어려움 
        * 최악의 경우: Order 조회할때 연관된 애들을 모두 db에서 끌어와버림
        * 특히 JPQL을 실행할때 `N+1 문제` 자주 발생 (하나 실행했는데 N개가..)
    * 실무에서 모든 연관관계는 지연로딩(`LAZY`)로 설정해야함 
    * 연관된 엔티티를 함께 DB에서 조회해야 하면, fetch join 또는 엔티티 그래프 기능을 사용
    * **@XToOne(OneToOne, ManyToOne) 관계는 기본이 즉시로딩 -> 지연로딩으로 직접 설정해야함** 
3. 컬렉션은 필드에서 초기화 
    * nullpointerexception 문제에서 안전함
    * 하이버네이트는 엔티티를 영속화할 때, 컬렉션을 감싸서 하이버네이트가 제공하는 내장 컬렉션으로 변경함 
    * 컬렉션을 가급적 변경하지 말자 -> 하이버네이트가 원하는 메커니즘대로 제대로 동작을 못 할 수 있음 
4. 테이블, 컬럼명 생성 전략
    * 하이버네이트 기존 구현: 엔티티의 필드명을 그대로 테이블 명으로 사용 
        * SpringPhysicalNamingStrategy 사용 
        * ex. orderDate -> order_date 로 됨 
