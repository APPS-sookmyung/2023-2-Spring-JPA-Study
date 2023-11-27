# API 개발 고급
조회에 있어서 성능 최적화를 하자 
수정과 생성은 거의 문제가 발생하지 않는 편 

## 조회용 샘플 데이터 입력
* application.yml에서 ddl-auto를 create로 설정하면 만들어져 있던 table을 다 drop하고 다시 create한다 
* 에러 해결 
    * 조회용 샘플 데이터 입력에서 /orders로 주문내역 페이지를 들어갔을 때 

    *An error happened during template parsing (template: "class path resource [templates/order/orderList.html]")라는 500 에러가 뜨는데 
    * 이는 orderList.html에서 
        ```html
        <option th:each=
                    "status : ${T(com.jpabook.jpashop.domain.OrderStatus).values()}"
                    th:value="${status}"
                    th:text="${status}">option
        </option>
        ```

        com.jpabook.jpashop.domain.OrderStatus 로 자신이 설정한 패키지 구조에 맞게 수정하시면 됩니다! (기존에는 그냥 jpabook.jpashop.domain.OrderStatus라 되어있었음)

    * 참고: https://www.inflearn.com/questions/670263/%EC%A3%BC%EB%AC%B8%EB%82%B4%EC%97%AD-getmapping-parsing-error-%EA%B4%80%EB%A0%A8-%EC%A7%88%EB%AC%B8-%EB%93%9C%EB%A6%BD%EB%8B%88%EB%8B%A4

## 지연로딩과 조회 성능 최적화 
주문+ 배송정보+회원을 조회하는 api 
지연로딩 때문에 발생한느 성능 문제를 단계적으로 해결 
**왕 중요 대박 중요** 실무에서 JPA 사용하려면 100% 이해해야함 

### 간단한 주문조회 V1: 엔티티를 직접 노출 
* 아래와 같이 코드를 작성하면 무한루프에 빠지게 됨 
    ```java
        @GetMapping("/api/v1/simple-orders")
        public List<Order> ordersV1(){
            List<Order> all=orderRepository.findAllByCriteria(new OrderSearch());
            return all;
        }
    ```
    * Order 안에 Member가 있음 -> Member 안에 Orders가 있음 -> Order 안에 Member가 ... => 무한루프를 돌며 객체를 뽑아내게 됨 (`양방향 연관관계 문제`)
    * 해결방법: 양방향 연관관계일 경우 한 쪽에 `@JsonIgnore`를 해야함
        ```java
            //Member.java
            @JsonIgnore
            @OneToMany(mappedBy = "member") //매핑된 거울이라는 의미
            private List<Order> orders = new ArrayList<>();
        ```
        ```java
        //Delivery.java
            @JsonIgnore
            @OneToOne(mappedBy = "delivery",fetch = FetchType.LAZY)
            private Order order;
        ```
        ```java
        //OrderItem.java
            @JsonIgnore
            @ManyToOne(fetch = FetchType.LAZY)
            @JoinColumn(name="order_id")
            private Order order;
        ```
    * 그러나 또 에러(500에러) 뜸(ByteBuddyInterceptor/InvalidDefinitionException)
        * 이유: **Order에서 Member가 지연로딩(fetch=LAZY)임** 
            * 지연로딩은 DB에서 직접 꺼내오는 것이 아님 
            * Proxy 라이브러리를 사용하여 가짜 new ProxyMember()를 Member에 넣어둠 (byteBuddy)
                * 우리 눈에 안보이지만 아래처럼 되어있는 것! 
                    ```java
                    private Member member=new ByteBuddyInterceptor();
                    ```
            * proxy 객체를 가짜로 넣어놓고 뭔가의 멤버 객체를 꺼내갈때 그때 db에 멤버객체 sql을 날려서 멤버 객체 값을 가져와서 채워주는 것
            * /simple-orders로 했을때 500에러가 뜨는 이유는 Jackson 라이브러리(json라이브러리)가 루프를 돌리는데 순수한 Member가 아니고 ByteBuddyInterceptor여서 오류가 난 것 
        * 해결: Hibernate5Module 사용 (build.gradle에 의존성을 추가하고 아래와 같이 @Bean으로 등록 후 사용)
            ```java
            	@Bean
                Hibernate5Module hibernate5Module(){
                    return new Hibernate5Module(); 
                }
            ```
            * 기본적으로 지연로딩을 무시하도록 함 (옵션을 넣어서 지연로딩도 강제로 로딩되게 할 수 있긴 함!)
* lazy 강제 초기화 방법
    ```java
        @GetMapping("/api/v1/simple-orders")
        public List<Order> ordersV1(){
            List<Order> all=orderRepository.findAllByCriteria(new OrderSearch());
            for (Order order: all){
                order.getMember().getName();
                //order.getMember()까지는 proxy 객체
                //.getName()을 하면 실제 name을 가져와야하므로 lazy 강제 초기화됨

                order.getDelivery().getAddress();
            }
            return all;
        }
    ```
* 그러나 이 방법은 엔티티를 그대로 노출시키기 때문에 api 스펙이 바뀌어버려 **좋지 않음** + 사용하지 않는 것(카테고리 등)까지 가져와서 성능 상 좋지도 않음 
* Hibernate5Module 사용보다는 `DTO`로 변환하는 것이 더 좋은 방법 
* 지연로딩(LAZY)를 피한다고 즉시 로딩(EAGER)으로 설정하는 것은 좋지 않음 
    * 연관관계가 필요 없는 경우에도 데이터를 항상 조회해서 성능 문제 발생 가능
    * 성능 튜닝이 매우 어려워짐 
    * 성능 최적화가 필요한 경우 페치 조인을 사용해라 

### 에러 해결
1. 조회용 샘플 데이터 입력에서 /orders로 주문내역 페이지를 들어갔을 때 
    * An error happened during template parsing (template: "class path resource [templates/order/orderList.html]")라는 500 에러가 뜨는데 
    * 이는 orderList.html에서 
        ```html
        <option th:each=
                    "status : ${T(com.jpabook.jpashop.domain.OrderStatus).values()}"
                    th:value="${status}"
                    th:text="${status}">option
        </option>
        ```

        com.jpabook.jpashop.domain.OrderStatus 로 자신이 설정한 패키지 구조에 맞게 수정하시면 됩니다! (기존에는 그냥 jpabook.jpashop.domain.OrderStatus라 되어있었음)

    * 참고: https://www.inflearn.com/questions/670263/%EC%A3%BC%EB%AC%B8%EB%82%B4%EC%97%AD-getmapping-parsing-error-%EA%B4%80%EB%A0%A8-%EC%A7%88%EB%AC%B8-%EB%93%9C%EB%A6%BD%EB%8B%88%EB%8B%A4
2. hibernate5module을 build.gradle에 추가하고 sync를 했는데 갑자기 `could not find com.github.gavlyukovskiy:p6spy-spring-boot-starter:0.0.1-snapshot.`라는 에러가 떴다. 기존에 설치했던 p6spy의 버전 이슈(?)인 것 같아서 아래의 글을 참고하여 고쳤다 
    * `implementation 'com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.5.6'`
    * 스프링 3점대 기준 build.gradle 버전 참고: https://www.inflearn.com/questions/779498/%EC%8A%A4%ED%94%84%EB%A7%81-%EB%B6%80%ED%8A%B8-3-0-querydsl-%EC%84%A4%EC%A0%95-%EA%B4%80%EB%A0%A8 
