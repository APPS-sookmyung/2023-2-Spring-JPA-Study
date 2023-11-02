# 애플리케이션 구현 준비 
## 애플리케이션 아키텍처 
* 계층형 구조 사용 
    * controller, web: 웹 계층
    * service: 비즈니스 로직, 트랜잭션 처리 
    * repository: JPA를 직접 사용하는 계층, 엔티티 매니저 사용
    * domain: 엔티티가 모여있는 계층. 모든 계층에서 사용 
    * 단방향으로 흐르도록. controller가 repository에도 바로 접근 가능 

# 회원 도메인 개발 
## 회원 리포지토리 개발 
* MemberRepository
    ```java
    public List<Member> findAll(){
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }
    ```
    * createQuery()는 엔티티 객체를 대상으로 쿼리를 함. 
    * FROM의 대상이 테이블이 아닌 **`객체(엔티티)`**
    ```java
    @Repository
    public class MemberRepository{}
    ```
    * @Repository는 @SpringBootApplication의 하위 컴포넌트 
    * 이를 통해 스프링 빈에 등록하게 됨 
    ```java
    @PersistenceContext
    private EntityManager em; 
    ```
    * 이 어노테이션이 있으면 JPA의 엔티티 매니저를 주입해줌 (아주 편하다!)
    ```java
    public Long save(Member member){
        em.persist(member); //persist하는 순간 영속성 컨텍스트 객체를 올림 
        return member.getId();
    }
    ```
    * persist하는 순간 영속성 컨텍스트를 올림 
    * 영속성 컨텍스트가 뭐였는지 까먹어서 해당 블로그 글을 참고하여 공부했습니다 
        * https://velog.io/@neptunes032/JPA-%EC%98%81%EC%86%8D%EC%84%B1-%EC%BB%A8%ED%85%8D%EC%8A%A4%ED%8A%B8%EB%9E%80
        * 영속성 컨텍스트: 엔티티를 영구 저장하는 환경 
            * 애플리케이션과 데이터베이스 사이에서 객체를 보관하는 가상의 데이터베이스 같은 역할 
            * 엔티티 매니저를 통해 엔티티를 저장하거나 조회하면 엔티티 매니저는 영속성 컨텍스트에 엔티티를 보관하고 관리  
## 회원 서비스 개발 
* `@Transactional` : JPA의 모든 데이터 변경이나 로직 변경은 트랜잭션 안에서 실행되어야함. 
    * **@Transactional(readOnly = true)** : jpa가 조회할때의 성능을 최적화 가능 
        * db에게 읽기 전용이니 리소스 많이 쓰지 말고 읽기 모드로 읽어라 하고 알려주기 가능함 
* **`@Autowired`**
    ```java
    @Autowired //스프링 빈에 등록되어있는 memeberRepository를 injection [field injection]
    private MemberRepository memberRepository;
    ```
    * field injection: 스프링 빈에 등록되어있는 memeberRepository를 injection 
    * 단점: 멤버 리포지토리를 바꿀 수가 없음 
    * 다른 방법 존재 1: `setter injection`
        ```java
            @Autowired
            public void setMemberRepository(MemberRepository memberRepository) {
                this.memberRepository = memberRepository;
            }
        ``` 
        * 장점: 테스트 코드같은거 작성할때 가짜 repository같은거 직접 주입 가능 (그냥 필드 인젝션은 주입하기가 어려움)
        * 단점: 애플리케이션을 돌릴때 다른 사람이 바꿀 수도 있음 
            * 실제로 애플리케이션을 개발해서 돌릴때는 이미 레포지토리를 완성하여 변경할 일이 없음 
    * 다른 방법 존재 2: (요새 권장) 생성자 injection
        ```java
            @Autowired
            public MemberService(MemberRepository memberRepository) {
                this.memberRepository = memberRepository;
            }
        ```
        * 생성자로 생성할떄 완성이 되어버리기 떄문에 set을 통해 레포지토리를 바꿀 수 없음
        * 테스트 케이스 작성할때 주입해야하는 것을 까먹거나 하는 것을 방지 가능 
    * 변경될 일이 없으므로 final 선언 권장. 
        ```java
        private final MemberRepository memberRepository;
        ```
        * 컴파일 시점을 체크해줄 수 있음 (생성자에 주입안하면 빨간 줄 뜸)
    * lombok에 `@AllArgsConstructor` 라는 어노테이션
            * 필드(private final MemberRepository memberRepository)만으로 생성자를 자동으로 만들어줌
    * lombok에 `@RequiredArgsConstructor` 라는 어노테이션 
        * final에 있는 필드만을 가지고 생성자를 만들어줌 
        * `@AllArgsConstructor` 보다 효율적 
## 회원 기능 테스트 
* 최근 스프링 부트는 JUnit5를 사용하기 때문에 @RunWith가 아닌 @ExtendWith 사용해야함 (참고글: https://jordy-torvalds.tistory.com/102 )
    * 그러나 @SpringBootTest를 사용하면 @ExtendWith(SpringExtension.class) 생략 가능
    ```java
    @ExtendWith(SpringExtension.class)
    @SpringBootTest
    public class MemberServiceTest { } 
    ```
* 엔티티 매니저에 persist를 한다고 db에 insert문이 안나감 (db 전략마다 좀 다르긴 하지만..)
    * database가 트랜잭션 커밋이 될떄 flush가 되면서 쿼리가 작동하는 것 
    * 그런데 spring에서 @Transactional은 트랜잭션 커밋을 안하고 rollback을 해버림. 
    * 따라서 `@Rollback(false)`를 통해 insert문 쿼리를 작동시킴 
        * rollback을 한다는 것은 어떠한 쿼리문도 날리지 않음. -> 영속성 컨텍스트를 flush하지 않는다. 
    * 만약 **rollback이지만** 쿼리를 날리는 것을 눈으로 확인하고 싶다면 EntityManager을 주입시킨 후 `em.flush();`를 호출하면 됨. 
        ```java
            @Autowired
            EntityManager em; 

            @Test
            public void 회원가입() throws Exception{
                //given
                Member member = new Member();
                member.setName("kim");

                //when
                Long savedId= memberService.join(member);

                //then
                em.flush(); 
                assertEquals(member, memberRepository.findOne(savedId));
            }
        ```
    * rollback하는 이유: 테스트이기 때문에 실제로 db에 쿼리문을 날리면 안되므로 
    * rollback false일때 쿼리문 확인하고 싶으면: db로 직접 들어가서 확인해보자 
* 중복 회원 예외 검사: try-catch문을 통해 에러를 발생시켜 exception으로 넘긴다. 
    ```java
    @Test
    public void 중복_회원_예외() throws Exception{
        //given
        Member member1= new Member();
        member1.setName("kim1");

        Member member2= new Member();
        member2.setName("kim1"); //중복회원을 넣자 -> exception을 테스트에서 발생시키자

        //when
        memberService.join(member1);
        try{
            memberService.join(member2); //에러가 발생해야함
        } catch(IllegalStateException e){
            return;
        }
        //then
        fail("예외가 발생해야한다");
    }
    ```
    * JUnit4에서는 @Test(expected=IllegalStateExcpetion.class)로 exception을 넘겨줄 수 있었으나 현재 최신 버전의 스프링부트에서 JUnit5로는 다르게 해야한다. (참고글: https://jinioh88.tistory.com/57 )
        ```java
            @Test
            public void 중복_회원_예외() throws Exception{
                //given
                Member member1= new Member();
                member1.setName("kim1");

                Member member2= new Member();
                member2.setName("kim1"); //중복회원을 넣자 -> exception을 테스트에서 발생시키자

                //when
                memberService.join(member1);


                //then
                //fail("예외가 발생해야한다"); //여기로 오면 안됨
                assertThrows(IllegalStateException.class,()->{
                    memberService.join(member2); //에러가 발생해야함
                });
            }
        ```
* 실제 db에 쿼리문을 날리지 않고 가상의 메모리 db를 만들어 test할 수 있음 
    * test 폴더에 /resources/ 에 application.yml을 그대로 복붙 후 아래와 같이 in memory db 주소로 url 변경 
        ```java
        spring:
          datasource:
              url: jdbc:h2:mem:test
              username: sa
              password:
              driver-class-name: org.h2.Driver
        ``` 
    * 그런데 아래와 같은 설정이 없어도 됨. 스프링부트가 기본적인 설정이 없으면 자동으로 메모리 보드로 돌려버리기 때문. -> 테스트가 정상 통과하는 것을 확인 가능. 
        ```java
          datasource:
            url: jdbc:h2:mem:test
            username: sa
            password:
            driver-class-name: org.h2.Driver

            jpa:
                hibernate:
                ddl-auto: create
                properties:
                hibernate:
                    #show_sql: true
                    format_sql: true
        ```

# 상품 도메인 개발 
## 상품 엔티티 개발 (비즈니스 로직 추가)
* 엔티티가 자체가 해결할 수 있는 것은 엔티티에서 처리하도록 하는 것(비즈니스 로직 추가)이 좋음 -> 응집도 굿
    * item interface에 아래와 같은 코드를 추가
        ```java
            //비즈니스 로직
            /**
            * 재고 증가
            */
            public void addStock(int quantity){
                this.stockQuantity+=quantity;
            }

            /**
            * stock 감소
            */
            public void removeStock(int quantity){
                int restStock= this.stockQuantity-quantity;
                if (restStock <0){
                    throw new NotEnoughStockException("need more stock");
                }
                this.stockQuantity=restStock;
            }
        ```