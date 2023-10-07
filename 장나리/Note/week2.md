# 섹션 2 도메인 분석 설계

## 1.  요구사항 분석

**기능 목록**

- 회원 기능
    - 회원 등록
    - 회원 조회
- 상품 기능
    - 상품 등록
    - 상품 수정
    - 상품 조회
- 주문 기능
    - 상품 주문
    - 주문 내역 조회
    - 주문 취소
- 기타 요구사항
    - 상품은 재고 관리가 필요하다.
    - 상품의 종류는 도서, 음반, 영화가 있다.
    - 상품을 카테고리로 구분할 수 있다.
    - 상품 주문시 배송 정보를 입력할 수 있다.

## 2. 도메인 모델과 테이블 설계

<img width="783" alt="스크린샷 2023-10-03 오전 11 15 57" src="https://github.com/APPS-sookmyung/2023-2-Spring-JPA-Study/assets/90668082/87f57900-036e-407a-ac21-be38116b75d4">

- **회원, 주문, 상품의 관계**
    - 회원은 여러 상품을 주문 가능, 한 번 주문할 때 여러 상품을 선택할 수 있으므로 주문과 상품은 다대다 관계
    - 하지만 이런 다대다 관계는 관계형 데이터베이스는 물론이고 엔티티에서도 거의 사용하지 않음
    - 주문상품이라는 엔티티를 추가해서 다대다 관계를 일대다, 다대일 관계로 풀어내야 함.
- **상품 분류**
    - 상품은 도서, 음반, 영화로 구분되는데 상품이라는 공통 속성을 사용하므로 상속 구조로 표현

**회원 엔티티 분석**

<img width="1000" alt="1" src="https://github.com/APPS-sookmyung/2023-2-Spring-JPA-Study/assets/90668082/e9f595ff-63dc-4a6e-b047-97942100be35">

- 공통적으로 id 값 있음
- **회원(Member):** 이름과 임베디드 타입인 주소( Address ), 그리고 주문( orders ) 리스트를 가짐
- **주문(Order)**
    - 한 번 주문시 여러 상품을 주문할 수 있으므로 주문과 주문상품( OrderItem )은 일대다 관계.
    - 주문은 상품을 주문한 회원과 배송 정보, 주문 날짜, 주문 상태( status )를 가짐
    - 주문 상태는 열거형을 사용했는데 주문( ORDER ), 취소( CANCEL ) 표현.
- **주문상품(OrderItem):** 주문한 상품 정보와 주문 금액( orderPrice ), 주문 수량( count ) (보통 OrderLine , LineItem 으로 많이 표현함)
- **상품(Item)**
    - 이름, 가격, 재고수량( stockQuantity )
    - 상품을 주문하면 재고수량 감소
    - 상품의 종류로는 도서, 음반, 영화가 있는데 각각은 사용하는 속성이 조금씩 다름
- **배송(Delivery)**
    - 주문시 하나의 배송 정보 생성
    - 주문과 배송은 일대일 관계
- **카테고리(Category)**
    - 상품과 다대다 관계
    - parent , child로 부모, 자식 카테고리 연결(계층구조)
- **주소(Address)**: 값 타입(임베디드 타입). 회원과 배송(Delivery)에서 사용
- 주의 : 회원을 통해서 항상 주문이 일어나는 것이 아니라 주문을 생성할 때 회원이 필요한 것!

참고: 회원이 주문을 하기 때문에 회원이 주문 리스트를 가져야한다 ?

No! 객체 세상은 실제 세계와 다름. 회원이 주문을 참조하지 않고, 주문이 회원을 참조하는 것으로 충분. 일대다, 다대일의 양방향 연관관계를 설명하기 위해서 추가한 예제.

**회원 테이블 분석**

<img width="1000" alt="2" src="https://github.com/APPS-sookmyung/2023-2-Spring-JPA-Study/assets/90668082/ddaac196-a503-4392-abfe-08960d29d6df">

- **MEMBER**: 회원 엔티티의 Address 임베디드 타입 정보(city, street, zipcode)가 회원 테이블에 그대로 들어감. DELIVERY 테이블도 마찬가지.
- **ITEM**: 앨범, 도서, 영화 타입을 통합해서 하나의 테이블(**싱글 테이블**). DTYPE 컬럼으로 타입 구분
- 싱글 테이블 : 성능 잘나옴
- **ORDERS** : ORDER BY 예약어 때문에 주로 order 대신 orders 사용
- **CATEGORY_ITEM** : 다대다 관계를 없애기 위해 맵핑 테이블 추가

**참고: 실제 코드에서는 DB에 소문자 + _(언더스코어) 스타일을 사용** 

데이터베이스 테이블명, 컬럼명에 대한 관례는 회사마다 다름. 보통은 대문자 + _(언더스코어)나 소문자+ _(언더스코어) 방식 중에 하나를 지정해서 일관성 있게 사용. **실제 코드에서는 소문자 + _(언더스코어) 스타일 사용 !**

**연관관계 매핑 분석**

- **회원과 주문**
    - 일대다 , 다대일의 양방향 관계
    - 따라서 연관관계의 주인을 정해야 하는데, 외래 키가 있는 주문을 연관관계의 주인으로 정하는 것이 좋다
    - 그러므로 Order.member 를 ORDERS.MEMBER_ID 외래 키와
    매핑한다.
    - 회원의 orders는 조회용
- **주문상품과 주문**
    - 다대일 양방향 관계
    - 외래 키가 주문상품에 있으므로 주문상품이 연관관계의 주인이다.
    - 그러므로 OrderItem.order 를 ORDER_ITEM.ORDER_ID 외래 키와 매핑한다.
- **주문상품과 상품**
    - 다대일 단방향 관계
    - OrderItem.item 을 ORDER_ITEM.ITEM_ID 외래 키와 매핑
- **주문과 배송**
    - 일대일 양방향 관계
    - Order.delivery 를 ORDERS.DELIVERY_ID 외래 키와 매핑한다.
- **카테고리와 상품**
    - @ManyToMany 를 사용해서 매핑한다.(실무에서 @ManyToMany는 사용하지 말자. 여기서는 다대다 관계를 예제로 보여주기 위해 추가했을 뿐이다)

**참고: 외래 키가 있는 곳을 연관관계의 주인으로 정해라.**

연관관계의 주인은 단순히 외래 키를 누가 관리하느냐의 문제. 비즈니스상 우위에 있다고 주인으로 정하면 안됨! 

예를 들어 자동차와 바퀴가 있으면, 일대다 관계에서 항상 다쪽에 외래 키가 있음 : 바퀴가 연관관계의 주인

자동차를 주인으로 정하면 관리와 유지보수가 어렵고, 추가적으로 별도의 업데이트 쿼리가 발생하는 성능 문제

## 3. 엔티티 클래스 개발

- 예제에서는 설명을 쉽게하기 위해 엔티티 클래스에 Getter, Setter를 모두 열고, 최대한 단순하게 설계
- 실무에서는 가급적 Getter는 열어두고, Setter는 꼭 필요한 경우에만 사용하는 것을 추천

참고: 이론적으로 Getter, Setter 모두 제공하지 않고, 꼭 필요한 별도의 메서드를 제공하는게 가장 이상적. 하지만 실무에서 엔티티의 데이터는 조회할 일이 너무 많으므로, Getter는 모두 열어두는 것이 편함. Getter는 아무리 호출해도 호출 하는 것 만으로 어떤 일이 발생하지 않지만 Setter는 호출하면 데이터가 변함. Setter를 막 열어두면 가까운 미래에 엔티티가 도대체 왜 변경되는지 추적하기 어렵. 엔티티를 변경할 때는 Setter 대신에 변경 지점이 명확하도록 변경을 위한 비즈니스 메서드를 별도로 제공해야 함.

- Address
    - JPA 내장 타입 → @Embeddable
    - 회원 엔티티에서
        
        ```java
        // Member.java
        @Embedded
                private Address address;
        ```
        
    - Embeddable이나 Embedded 둘 중 하나만 해도 됨
    - 변경이 되면 안되니까 만들어질때 값이 세팅되고 바뀌면 안됨.
        - Getter만, Setter는 x
        - JPA 스펙상 엔티티나 임베디드 타입( @Embeddable )은 자바 기본 생성자(default constructor)를 public 또는 protected 로 설정해야 함.
        - public 으로 두는 것 보다는 protected 로 설정하는 것이 그나마 더 안전
        - JPA가 이런 제약을 두는 이유는 JPA 구현 라이브러리가 객체를 생성할 때 리플랙션 같은 기술을 사용할 수 있도록 지원해야 하기 때문
- @JoinColumn : 외래키를 매핑할 때 사용
- 외래 키가 있는 곳이 연관관계의 주인 !!!
- mappedBy = 연관관계의 주인이 아닌 것을 표시하는 설정
- LocalDateTime
    - Date 타입은 날짜 관련 annotation 매핑 해야함
    - LocalDateTime은 Hibernate가 알아서 지원해줌
- Item : 상속관계 매핑
    - Item 을 추상클래스로
    - Book, Album, Movie → extends Item
    - 상속관계 전략 지정 : @Inheritance**(strategy=InheritanceType.XXX)**
        - InheritanceType 종류
            - JOINED : 테이블 정규화에 유리
            - SINGLE_TABLE : 쿼리가 단순, `join`을 사용하지 않아도 되기 때문에 성능 좋음
            - TABLE_PER_CLASS : 서브 타입을 명확하게 구분하여 처리할 때 효과적이고 컬럼에 `not null`을 명시하여 데이터를 제한 가능
    - @DiscriminatorColumn(name="DTYPE") : 부모 클래스에 선언, 하위 클래스를 구분하는 용도의 컬럼
    - @DiscriminatorValue("XXX") : 하위 클래스에 선언, 엔티티를 저장할 때 슈퍼타입의 구분 컬럼에 저장할 값을 지정. 어노테이션을 선언하지 않을 경우 기본값으로 클래스 이름
- @Enumerated(EnumType.STRING) : enum 이름 값을 DB에 저장
- @Enumerated(EnumType.ORIGINAL) : enum 순서(숫자) 값을 DB에 저장
    - 새로운 값이 추가 되었을 때 문제 발생!! 절대 사용x
- 일대일의 경우 주로 액세스 하는 곳에 외래키
- @ManyToMany
    
    ```java
    //Category.java
    @ManyToMany
    @JoinTable(name = "category_item",
          joinColumns = @JoinColumn(name = "category_id"),
          inverseJoinColumns = @JoinColumn(name = "item_id"))
    private List<Item> items = new ArrayList<>();
    ```
    - JoinTable : 별도의 테이블을 만들어서 각 테이블의 외래키를 가지고 연관관계를 관리
        - name : 조인 테이블 명
        - joinColumns : 현재 엔티티를 참조하는 외래키
        - inverseJoinColumns : 반대방향 엔티티를 참조하는 외래키
- **실무에서는 @ManyToMany 를 사용하지 말자!!!!!**
    - @ManyToMany 는 편리한 것 같지만, 중간 테이블( CATEGORY_ITEM )에 컬럼을 추가할 수 없고, 세밀하게 쿼리를 실행하기 어렵기 때문에 실무에서 사용하기에는 한계가 있다.
    - 중간 엔티티(CategoryItem)를 만들고 @ManyToOne , @OneToMany 로 매핑해서 사용
    - **다대다 매핑을 일대다, 다대일 매핑으로 풀어 내서 사용**

## 5. 엔티티 설계 시 주의점

- **엔티티에는 가급적 Setter를 사용하지 말자**
    - Setter가 모두 열려있다. 변경 포인트가 너무 많아서, 유지보수가 어렵. 나중에 리펙토링으로 Setter 제거!!!!!
- **모든 연관관계는 지연로딩으로 설정!**
    - 즉시로딩( EAGER )은 예측이 어렵고, 어떤 SQL이 실행될지 추적하기 어렵
    - 특히 JPQL을 실행할 때 N+1 문제가 자주 발생
    - 실무에서 모든 연관관계는 **지연로딩(LAZY)**으로!!!!
    - 연관된 엔티티를 함께 DB에서 조회해야 하면, fetch join 또는 엔티티 그래프 기능 사용
    - @XToOne(OneToOne, ManyToOne) 관계는 기본이 즉시로딩이므로 직접 지연로딩으로 설정해야 함
        
        ```java
        @OneToOne(fetch = FetchType.LAZY)
        @ManyToOne(fetch = FetchType.LAZY)
        ```
        
- **컬렉션은 필드에서 초기화 하자.**
    - 컬렉션은 필드에서 바로 초기화 하는 것이 안전
    - null 문제에서 안전
    - 하이버네이트는 엔티티를 영속화 할 때, 컬랙션을 감싸서 하이버네이트가 제공하는 내장 컬렉션으로 변경
    - 처음 생성할 때 딱 그냥 놔두고 이 컬렉션 자체를 바꾸지 말자
    - 만약 getOrders() 처럼 임의의 메서드에서 컬력션을 잘못 생성하면 하이버네이트 내부 메커니즘에 문제가 발생 가능성 o
    - 필드레벨에서 생성하는 것이 가장 안전하고, 코드도 간결
- **테이블, 컬럼명 생성 전략**
    - 스프링 부트에서 하이버네이트 기본 매핑 전략을 변경해서 실제 테이블 필드명은 다름
    - 하이버네이트 기존 구현: 엔티티의 필드명을 그대로 테이블의 컬럼명으로 사용 ( SpringPhysicalNamingStrategy )
        1. 카멜 케이스 → 언더스코어(memberPoint member_point)
        2. .(점) →  _(언더스코어)
        3. 대문자 → 소문자
    - 적용 2단계
        1. 논리명 생성: 명시적으로 컬럼, 테이블명을 직접 적지 않으면 ImplicitNamingStrategy 사용`spring.jpa.hibernate.naming.implicit-strategy` : 테이블이나, 컬럼명을 명시하지 않을 때 논리명 적용
        2. 물리명 적용:
            
            `spring.jpa.hibernate.naming.physical-strategy` : 모든 논리명에 적용됨, 실제 테이블에 적용 (username usernm 등으로 회사 룰로 바꿀 수 있음)
            
- ****영속성 전이(CASCADE)****
    
    ```java
    // Order.java
    @OneToMany(mappedBy = "order", **cascade = CascadeType.ALL)**
    private List<OrderItem> orderItems = new ArrayList<>();
    ```
    
    - orderItems에 데이터를 넣어두고 order를 저장하면 orderItems도 같이 저장됨!
- 연관관계 편의 메서드
    - 양방향 연관관계에서 양쪽 객체의 관계를 모두 채워주기 위한 편의 메소드
    - 한쪽에서만 두 관계를 설정하도록 하는 것이 안전
    - 핵심적으로 컨트롤하는 쪽에 설정하는 것이 좋음