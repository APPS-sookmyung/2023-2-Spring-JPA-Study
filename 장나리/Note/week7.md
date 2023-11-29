# 섹션 2 API 개발 고급 - 준비

## 1. API 개발 고급 소개

- 페이징 처리 - 쿼리 너무 많이 나가는 경우 최적화
- 레이지 로딩 이셉션
- 등록과 수정은 성능 문제 거의 발생하지 않음
- 조회에서 많이 발생
- n+1 문제 해결
- 단계별로 최적화하는 방법 실습 예정
- 컬렉션 조회 최적화
    - 일대일이나 다대일처럼 join 했는데 데이터가 안늘어나는 경우에는 크게 성능에 이슈 없음
    - 일대다 join 같은 경우 데이터가 확 늘어남
- 페이징과 한계 돌파
    - 조인하고 페이징 쿼리 날리기 어려움
- OSIV와 성능 최적화
    - 지연 로딩에서 편함
    - 사용안하면 lazy loading exception 자주 만남!!!

## 2. 조회용 샘플 데이터 입력

- User1
    - JPA1 BOOK
    - JPA2 BOOK
- User2
    - SPRING1 BOOK
    - SPRING2 BOOK

```java
@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    @PostConstruct
    public void init(){
        initService.dbInit1();
        initService.dbInit2();

    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService{

        private final EntityManager em;
        public void dbInit1() {
            Member member = createMember("userA", "서울", "1", "1111"); em.persist(member);

            Book book1 = createBook("JPA1 BOOK", 10000, 100);
            em.persist(book1);

            Book book2 = createBook("JPA2 BOOK", 20000, 100);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000, 2);
            Order order = Order.createOrder(member, createDelivery(member), orderItem1, orderItem2);
            em.persist(order);
        }

        public void dbInit2() {
            Member member = createMember("userB", "진주", "2", "2222"); em.persist(member);

            Book book1 = createBook("SPRING1 BOOK", 20000, 200);
            em.persist(book1);

            Book book2 = createBook("SPRING2 BOOK", 40000, 300);
            em.persist(book2);

            Delivery delivery = createDelivery(member);
            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 20000, 3);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 40000, 4);
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);

        }

        private Member createMember(String name, String city, String street,
                                    String zipcode) {
            Member member = new Member();
            member.setName(name);
            member.setAddress(new Address(city, street, zipcode));
            return member;
        }

        private Book createBook(String name, int price, int stockQuantity) {
            Book book = new Book();
            book.setName(name);
            book.setPrice(price);
            book.setStockQuantity(stockQuantity);
            return book;
        }

        private Delivery createDelivery(Member member) {
            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());
            return delivery;
        }
    }
}
```

- 이렇게 하고 서버 띄우면
    - 스프링의 컴포넌트 스캔 → 스프링 빈이 다 엮이고 → 스프링빈이 다 올라오고 나면 스프링이 `@PostConstructor` 호출 → `dbInit1()` 호출
- `dbInit()` : `init()`안에 그냥 넣으면 안되나?
    - 스프링 라이프사이클 때문에 트랜잭션 넣는게 안됨
    - 별도의 빈으로 등록해야 함!!
    

# 섹션 3 API 개발 고급 - 지연 로딩과 조회 성능 최적화

## 1. 간단한 주문 조회 V1: 엔티티를 직접 노출

- 주문과 연관된 배송 정보, 회원 조회하는 API
- 지연 로딩 때문에 발생하는 성능 문제를 단계적으로 해결해보자!!
- 실무에서 JPA를 사용하려면 100% 이해해야 함!!
- *Order*
- *Order -> Member : Many **to One***
- *Order -> Delivery : One **to One***
- Order → OrderItem : One **to Many** (컬렉션이라 복잡)
- OrderSimpleApiController
    - V1
        
        ```java
        @GetMapping("/api/v1/simple-orders")
        public List<Order> ordersV1(){
            List<Order> all = orderRepository.findAllByString(new OrderSearch());
            return all;
        }
        ```
        
        - 반환하면 그대로 가져옴
        - 검색 조건이 없기 때문에 주문을 다 들고 옴
        - 이렇게 하면 Order → Member → Orders → Member → … : 무한루프 …..
        - 이렇게 하면 절대 안됨!!!!!!
        - `@JsonIgnore` : 양방향 걸리는 곳에 다 넣어줘야 함
        - 그래도 에러 발생!! : Type Definition Error
            - `fetch = Lazy` : 지연 로딩 → 진짜 new 해서 멤버 객체를 가져오는게 아님
            - DB에서 끌고 오는게 아님
            - 프록시 라이브러리를 써서 프록시 멤버 객체를 생성해서 넣음 : bytebuddyInterceptor가 대신 들어가 있는 것
            - 지연로딩인 경우 hibernate야 아무것도 하지마!! : hibernate5 모듈 설치
            - **스프링 부트 3.0 이상: Hibernate5JakartaModule 등록**
            - `implementation 'com.fasterxml.jackson.datatype:jackson-datatype-hibernate5-jakarta'`
            - 버전은 빼도 됨 → 스프링 부트가 최적화된 버전 갖고 있음
            
            ```java
            // JpashopApplication
            @Bean
            Hibernate5JakartaModule hibernate5Module() {
                Hibernate5JakartaModule hibernate5JakartaModule = new Hibernate5JakartaModule();
                hibernate5JakartaModule.configure(Hibernate5JakartaModule.Feature.FORCE_LAZY_LOADING,true);
                return hibernate5JakartaModule;
            }
            ```
            
            - 대충 들어도 됨 ㅎㅎ 어차피 이렇게 하면 안됨
            - 성능에도 문제 : 사용하지 않는, 필요없는 것까지 다 가져옴
        - 엔티티를 외부로 노출하지 마라
        - hibernate5보다 DTO 사용하는게 좋음
        - 지연 로딩(LAZY)을 피하기 위해 즉시 로딩(EARGR)으로 설정하면 안된다
            - 즉시 로딩으로 설정하면 성능 튜닝이 매우 어려워 짐
            - 항상 지연 로딩을 기본으로 하고, 성능 최적화가 필요한 경우에는 페치 조인(fetch join 사용!!!

## 2. 간단한 주문 조회 V2: 엔티티를 DTO로 변환

- dto 반환
- dto가 엔티티를 파라미터로 받는 것은 크게 문제되지 않음.
    
    ```java
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> orderV2(){
        return orderRepository.findAllByString(new OrderSearch()).stream()
                .map(SimpleOrderDto::new)
                .collect(toList());
    }
    @Data
    static class SimpleOrderDto{
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
    
        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); // LAZY 초기화
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); // LAZY 초기화
        }
    }
    ```
    
    - 생성자에서 완성
        
- V2가 가진 문제(V1도) : lazy loading으로 인한 데이터베이스 쿼리가 너무 많이 호출됨 ..!!!
- Order, Member, Delivery(Address) - 3개 테이블 조회
    - **Order 조회** → stream으로 반복할 때 LAZY 초기화 : 영속성 컨텍스트가 멤버 아이디를 가지고 영속성 컨텍스트 찾음 없으면 쿼리 날림
    - Member 조회
    - Delivery 조회
    - 총 3번
    - 근데! Member, Delivery 보면 아이디 1번만 : 첫번째 주문서만 완성
    - 두번째 주문서도 3번 또 날려서 조회
- Order → SQL 1번 → 결과 주문 수 2개
- 루프가 2번 도는 것!
- 첫번째 루프 돌면서 레이지 로딩 초기화, 쿼리 3번 - 두번째 루프 돌면서 레이지 로딩 초기화 되고 쿼리 3번 : **n+1 문제!!!!!**
- 첫번째 쿼리의 결과로 n번 만큼 쿼리가 추가로 실행됨 : n+1 문제
- 쿼리가 총 1 + N + N번 실행됨
- 지연 로딩은 영속성 컨텍스트에서 조회하므로, 이미 조회된 경우 쿼리 생략
    - 줄어들긴 하는데 꼭 그렇진 않음

## 3. 간단한 주문 조회 V3: 엔티티를 DTO로 변환 - 페치 조인 최적화

- v2 : 쿼리 너무 많이 나가는 문제 (N+1 문제)
- v3 : 페치 조인

```java
//OrderRepository.java
public List<Order> findAllWithMemberDelivery() {
        return em.createQuery("select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class)
                .getResultList();
    }
```

- Order 조인 → Member, Delivery를 select 절에서 한번에 가져옴
    - 진짜 값을 가져옴 : fetch join
    - jpa에만 있는 문법
- 성능문제의 90%는 fetch join으로 해결가능!
- 엔티티를 페치 조인(fetch join)을 사용해서 쿼리 1번에 조회
- 페치 조인으로 order -> member , order -> delivery 는 이미 조회 된 상태 이므로 지연로딩X
- v2 → 5번, v3 → 1번
