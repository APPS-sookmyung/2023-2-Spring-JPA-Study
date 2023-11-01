# Section3. 애플리케이션 구현 준비

## **구현 요구사항**

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

## **애플리케이션 아키텍처**

![img](https://blog.kakaocdn.net/dn/zxUH1/btszuKXSdV8/BqdmdHvjzkYJFnRUYM77pK/img.png)

- 계층형 구조 사용
    - controller, web : 웹 계층
    - service : 비즈니스 로직, 트랜잭션 처리
    - repository : JPA를 직접 사용하는 계층, 엔티티 매니저 사용
    - domain : 엔티티가 모여 있는 계층, 모든 계층에서 사용
- 패키지 구조
    - jpabook.jpashop
        - domain
        - exception
        - repository
        - service
        - web
- 참고
    - controller은 repository에도 바로 접근할 수 있는 그러나 단방향으로 설정할 예정
- 개발 순서
    - 서비스/리포지토리 계층 개발 → 테스트 케이스 작성하여 검증 → 마지막에 웹 계층 적용
 
# Section4. 회원 도메인 개발
 
## 회원 리포지토리 개발

-   jpashop/src/main/java/jpabook/jpashop/repository/MemberRepository.java 생성

```
package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jpabook.jpashop.domain.Member;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MemberRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(Member member){ //jpa가 저장하는 로직
        em.persist(member);
    }

    public Member findOne(Long id) { //Member를 반환, 단권 조회
        return em.find(Member.class, id);
    }

    public List<Member> findAll(){ //리스트 조회
        return em.createQuery("select m from Member m", Member.class) //jpql이라는 것, entity 객체에 대해 query
                .getResultList();
    }

    public List<Member> findByName(String name){ //이름으로 회원 검색
        return em.createQuery("select m from Member m where m.name = :name", Member.class) //jpql이라는 것, entity 객체에 대해 query
                .setParameter("name", name)
                .getResultList();
    }

}
```

-   @Repository
    -   component 스캔에 의해 자동으로 spring bin으로 관리 된다
-   @PersistenceContext
    -   스프링이 EntityManager을 만들어 em에 주입한다

## 회원 서비스 개발

-   jpashop/src/main/java/jpabook/jpashop/service/MemberService.java 생성

```
package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service //컴포넌트 스캔의 대상이 되어 자동으로 스프링 빈 등록
@Transactional //(readOnly = false)
//@AllArgsConstructor //필드의 생성자 만들기
@RequiredArgsConstructor //final인 것들만 생성자 만들기
public class MemberService {

    private final MemberRepository memberRepository; //변경할 일 X

    //@Autowired //스프링이 스프링 빈에 등록되어 있는 MemberRepository를 주입 = field injection
    //spring에서는 생성자가 1개만 있는 경우에는 @Autowired 없이도 자동으로 injection 해줌
//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    //회원 가입
    @Transactional
    public Long join(Member member){
        validateDuplicateMember(member); //중복 회원 검증
        memberRepository.save(member);
        return member.getId(); //항상 값이 있다는 것이 보장됨
    }

    private void validateDuplicateMember(Member member) {
        //Exception
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    //회원 전체 조회
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }
    public Member findOne(Long memberId){ //단권 조회
        return memberRepository.findOne(memberId);
    }
}
```

-   @Service
    -   component 스캔의 대상이 되어 자동으로 spring bin 등록
-   @Transactional
    -   JPA의 모든 데이터 변경 or 로직들은 가급적이면 transaction 안에서 다 실행되어야 해서 사용
    -   import org.springframework.transaction.annotation.Transactional; 사용이 나음
    -   쓸 수 있는 dependency가 많음
    -   @Transactional(readOnly = true) 가 되면 JPA가 조회하는 곳에서는 성능을 더 최적화 함
    -   읽기에는 readOnly 넣어주기
    -   쓰기에는 readOnly넣지 말기

-   @Transactional을 이용해 "회원 리포지토리 개발" 에서 작성한 코드를 줄일 수 있다

```
package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private EntityManager em;

    public void save(Member member){ //jpa가 저장하는 로직
        em.persist(member);
    }

    public Member findOne(Long id) { //Member를 반환, 단권 조회
        return em.find(Member.class, id);
    }

    public List<Member> findAll(){ //리스트 조회
        return em.createQuery("select m from Member m", Member.class) //jpql이라는 것, entity 객체에 대해 query
                .getResultList();
    }

    public List<Member> findByName(String name){ //이름으로 회원 검색
        return em.createQuery("select m from Member m where m.name = :name", Member.class) //jpql이라는 것, entity 객체에 대해 query
                .setParameter("name", name)
                .getResultList();
    }

}
```

## 회원 기능 테스트

-   테스트 요구사항
    -   회원가입을 성공해야 한다
    -   회원가입 할 때 같은 이름이 있으면 예외가 발생해야 한다
-   jpashop/src/main/java/jpabook/jpashop/service/MemberService.java 생성

```
package jpabook.jpashop.service;

import io.micrometer.common.util.internal.logging.InternalLogLevel;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

//JUnit4
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional //롤백을 위해
public class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

    @Test(expected = IllegalStateException.class)
    //@Rollback(false) //insert문을 볼 수 있음, Transactional은 rollback을 하니까
    public void 회원가입() throws Exception{
        //given
        Member member = new Member();
        member.setName("cho");
        //when
        Long saveId =  memberService.join(member);
        //then
        assertEquals(member, memberRepository.findOne(saveId));
    }

    @Test
    public void 중복_회원_예외() throws Exception{
        //given
        Member member1 = new Member();
        member1.setName("cho");

        Member member2 = new Member();
        member2.setName("cho");
        //when
        memberService.join(member1);
/*        try{ //exception을 잡아주어야 함
            memberService.join(member2);
        } catch (IllegalStateException e){ //여기서 exception을 잡아 줌
            return;
        }*/
        memberService.join(member2); //예외가 발생해야 한다
        //then
        fail("예외가 발생해야 한다.");

    }
}
```

-   테스트 케이스 작성 툴
    -   //given
    -   //when
    -   //then
-    @RunWith(SpringRunner.class)
    -   스프링과 테스트 통합
-    @SpringBootTest
    -   스프링부트를 띄우고 테스트하게 하는 어노테이션
    -   위 어노테이션이 없으면 @Autowired 다 실패
-    @Transactional
    -   위 어노테이션을 사용하여 영속성 컨텍스트가 플러시를 안하게 하면/rollback을 하게 하면, insert query조차 나가지 않음
    -   반복 가능한 테스트를 지원하는 어노테이션
    -   각각의 테스트를 실행할 때마다 트랜잭션을 시작하고 테스트가 끝나면 트랜잭션을 강제로 롤백 
    -   @Rollback(false)를 덧붙여주면 insert query 확인이 가능함
-   rollback을 해야 하는 이유
    -   테스트는 반복해서 진행되어야 하기 때문에 DB에 데이터가 남으면 안돼서 insert 하고 다시 rollback이 된다

-   위 테스트 코드는 DB를 외부에 설치해야 하는 번거로움이 있다
    -   Memory DB 사용
    -   Java 안에 작은 DB를 만들어 띄우는 방법
    -   test 디렉토리 안에 resources 디렉토리 생성
        -   기본 운영 로직 : main의 resources 패키지에 우선권을 가짐
        -   테스트 실행 시 : test의 resources 패키지에 우선권을 가짐
-   아래와 같은 폴더 구조를 갖도록 함
    -   yml 파일은 main의 것을 복붙


```
spring:
  #h2 세팅
  datasource:
    url: jdbc:h2:mem:test #메모리 DB로 바꿔주기
    username: sa
    password:
    driver-class-name: org.h2.Driver

    #jpa 세팅
  jpa:
    hibernate:
      ddl-auto: create #자동으로 탭을 만들어주는, 애플리케이션 실행 시점에 가지고 있는 테이블을 지우고 다시 생성
    properties:
      hiberate:
        #show_sql: true #System.out에 출력
        format_sql: true

logging.level:
    org.hibernate.SQL: debug #JPA나 Hibernate가 생성하는 SQL이 모두 보이는, logger를 통해
```

# Section5. 상품 도메인 개발


## 상품 엔티티 개발 (비즈니스 로직 추가)

-   구현 기능
    -   상품 등록
    -   상품 목록 조회
    -   상품 수정
-   순서
    1.  상품 엔티티 개발 (비즈니스 로직 추가)
    2.  상품 리포지토리 개발
    3.  상품 서비스 개발, (상품 기능 테스트)
-   item.java 수정

```
package jpabook.jpashop.domain.item;

import jakarta.annotation.ManagedBean;
import jakarta.persistence.*;
import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED) //Single Table 전략
@DiscriminatorColumn(name = "dtype")
public class Item {
    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    // == 비즈니스 로직 == //
    public void addStock(int quantity){ //재고 수량 증가 로직
        this.stockQuantity += quantity;
    }

    public void removeStock(int quantity){
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0){
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }
}
```

-   jpashop/src/main/java/jpabook/jpashop/exception/NotEnoughStockException.java 생성

```
package jpabook.jpashop.exception;

public class NotEnoughStockException extends RuntimeException {
    public NotEnoughStockException() {
        super();
    }

    public NotEnoughStockException(String message) {
        super(message);
    }

    public NotEnoughStockException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotEnoughStockException(Throwable cause) {
        super(cause);
    }

}
```

-   재고를 변경해야 할 일이 있으면 핵심 비즈니스 메소드를 가지고 변경하게 해야함 → 가장 객체지향적인 것

## 상품 리포지토리 개발

-   jpashop/src/main/java/jpabook/jpashop/repository/ItemRepository.java 생성

```
package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item){
        if (item.getId() == null){ //완전 없던, 새로 생성된 객체에 대해서 가져온 상황
            em.persist(item);
        } else {
            em.merge(item); //update와 유사한 것, 어디선가 한 번 등록된 것을 가져온 상황
        }
    }

    public Item findOne(Long id){
        return em.find(Item.class, id);
    }

    public List<Item> findAll(){
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }
}
```

## 상품 서비스 개발

-   상품 서비스는 상품 리포지토리에 단순하게 위임만 하는 클래스이다
-   jpashop/src/main/java/jpabook/jpashop/service/ItemService.java 생성

```
package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }
    public List<Item> findItems() {
        return itemRepository.findAll();
    }
    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }
}
```
