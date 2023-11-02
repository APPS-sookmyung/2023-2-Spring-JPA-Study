# 섹션 3 애플리케이션 구현 준비

## 1.  구현 요구사항

**기능 목록**

- 회원 기능
    - 회원 등록
    - 회원 조회
- 상품 기능
    - 상품 등록
    - 상품 수정
    - 상품 조회
- 주문 기능
    - 상품 주문
    - 주문 내역 조회
    - 주문 취소

## 2.  애플리케이션 아키텍처
- 계층형 구조
    - Controller, web : 웹 계층
    - service : 비즈니스 로직, 트랜잭션 처리
    - repository : JPA직접 사용하는 계층, 엔티티 매니저 사용
    - Domain : 엔티티가 모여 있는 계층, 모든 계층에서 사용
- 패키지 구조
- jpabook.jpashop
    - domain
    - exception
    - repository
    - service
    - web
- controller는 repository에 바로 접근 가능
- 단방향
- 개발 순서 : 서비스, 리포지토리 계층 개발하고 (웹 제외) 테스트 케이스를 작성해서 검증, 마지막에 웹 계층 적용

# 섹션 4 회원 도메인 개발

## 1.  회원 리포지토리 개발

- 구현 기능
    - 회원 등록
    - 회원 목록 조회
- 순서
    - 회원 엔티티 코드 다시 보기
    - 회원 리포지토리 개발
    - 회원 서비스 개발
    - 회원 기능 테스트

---

- `@Repository` : Spring에서 제공하는 어노테이션, 컴포넌트 스캔에 의해 자동으로 스프링 빈으로 등록
- `@PersistenceContext` : JPA가 제공하는 표준 어노테이션
- EntityManager em : 스프링이 엔티티 매니저를 만들어 여기에 주입해줌
- findAll() : 전체 찾아서 리스트 → JPQL 작성, getResult
- JPQL : 테이블 대상으로 쿼리를 하는 SQL과 달리 entity를 대상으로 쿼리를 함.
- 이름으로 회원 조회 : parameter에 이름, where문 추가
    
    ```java
    public List<Member> findByName(String name){
            return em.createQuery("select m from Member m where m.name = :name", Member.class)
                    .setParameter("name",name)
                    .getResultList();
        }
    ```
    
- entity manager factory를 직접 주입하고 싶으면 `@PersistenceUnit`
- find(type, pk)

## 2. 회원 서비스 개발

- @Service : 스프링이 제공하는 어노테이션, 컴포넌트 스캔의 대상이 되어 자동으로 스프링빈 등록
- JPA의 모든 데이터 변경이나 로직들은 가급적이면 트랜잭션 안에서 실행되어야 함. → `@Transactional` (spring)
- `@Transactional(readOnly=true)` : 읽기 전용. 성능 최적화. 리소스 많이 안씀
- `@Autowired` : 바꾸지 못함. → setter injection
    
    ```java
    @Autowired
        public void setMemberRepository(MemberRepository memberRepository){
            this.memberRepository = memberRepository;
        }
    ```
    
    - 장점 : 테스트 코드 작성할때 mock 주입가능
    - 단점 : 보통 리포지토리 호출해서 개발하는 중간에 바꾸지 않음. 잘 동작하고 있는데 바꾸진 않음 → 생성자 인젝션 사용
- 생성자 인젝션
    
    ```java
    @Autowired
        public MemberService (MemberRepository memberRepository){
            this.memberRepository = memberRepository;
        }
    ```
    
    - memberRepository 바꾸지 못함
    - 테스트 케이스 작성할 때 MemberService가 의존하고 있는 걸 바로 알 수 있음
    - 생성자가 하나인 경우 생성자에 자동으로 인젝션을 해줌 → `@Autowired` 생략 가능
- *`private final* MemberRepository memberRepository;` : final 권장 → 컴파일 시점에 체크 가능
- `@AllArgsConstructor` : 생성자 만들어줌
- `@RequiredArgsConstructor` : final이 있는 것만 생성자 만들어줌
- repository에서 `@PersistenceContext` 대신`@RequiredContructor` 가능 → springdatajpa에서 지원되는 거

## 3. 회원 기능 테스트

- 회원 가입 성공
- 같은 이름이 있으면 에러 발생
- persist : insert문 x
    - 영속성 컨텍스트가 flush를 안함
    - rollback이 됨 → rollback(false) : db보면 들어가있는거 볼 수 있음
    - 디비에 쿼리 날리는 거 보고 싶으면 em.flush();
- 중복 로직
- `@Test(expected = IllegalStateException.*class*)` : 발생한 에러가 `IllegalStateException` 이어야 함.
- `@SpringBootTest` : 스프링 부트 띄운 상태로 테스트를 하려면 필요. 없으면 Autowired 다 실패
- `@Transactional` : transactional 걸고 테스트를 돌린 다음에 테스트 끝나면 다시 rollback.
- 테스트를 완전히 격리된 환경에서, java 안에 살짝 데이터베이스를 새로 만들어서 띄우는 Memory DB → 스프링 부트를 쓰면 공짜로 가능
    - test > resources 디렉토리 생성
    - application.yml 파일 복붙
    - url을 memory로
        - **H2** 사이트 → cheat sheet → In-Memory → `jdbc:h2:mem:test`
    - `runtimeOnly 'com.h2database:h2'` : 메모리 모드로 h2 띄울 수 있음
    
    ```java
    spring:
    #  datasource:
    #    url: jdbc:h2:mem:test
    #    username: sa
    #    password:
    #    driver-class-name: org.h2.Driver
    #
    #  jpa:
    #    hibernate:
    #      ddl-auto: create
    #    properties:
    #      hiberbate:
    ##          show_sql: true
    #        format_sql: true
    
    logging.level:
      org.hibernate.SQL: debug
      org.hibernate.orm.jdbc.bind: trace #스프링 부트 3.x, hibernate6
    ```
    
    - 이렇게 돌려도 똑같이 작동 → 별도의 설정이 없으면 메모리 모드로 돌아가기 때문!

# 섹션 5 상품 도메인 개발

## 1.  상품 엔티티 개발(비즈니스 로직 추가)

- **구현 기능**
    - 상품 등록
    - 상품 목록 조회
    - 상품 수정
- **순서**
    - 상품 엔티티 개발(비즈니스 로직 추가)
    - 상품 리포지토리 개발
    - 상품 서비스 개발
    - 상품 기능 테스트

---

- 재고 늘리고 줄이기
- 도메인 주도 설계 - 엔티티 자체가 해결할 수 있는 것들은 엔티티 안에 비즈니스 로직을 설계하는 것이 좋음 → 객체지향
- *`throw new* NotEnoughStockException("need more stock");` → `NotEnoughStockException` 파일 생성

## 2.  상품 리포지토리 개발

- item 은 처음에는 id가 없음 → id 없으면 `em.persist(item)`, id 있으면 `em.merge(item);`
- `em.persist` : 등록
- `em.merge` : update와 비슷

## 3.  상품 서비스 개발

- 상품 리포지토리에 단순하게 위임만 하는 클래스 → 위임만 하는 클래스에 만드는게 의미가 있을까 ?
