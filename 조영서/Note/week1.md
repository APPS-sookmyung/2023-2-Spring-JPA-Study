# 1주차 스터디

## **프로젝트 생성**

**1. SpringBoot Starter 접속 ([Spring Initializr)](https://start.spring.io/)**

**2. 설정**

![https://blog.kakaocdn.net/dn/m3v5z/btsvmxVOjEg/yEhLKK4y6ODjO6MYgNIfWK/img.png](https://blog.kakaocdn.net/dn/m3v5z/btsvmxVOjEg/yEhLKK4y6ODjO6MYgNIfWK/img.png)

![https://blog.kakaocdn.net/dn/8nQ5Q/btsvnf8lqkO/CjmjSeLLA21JMNkTrFTNWK/img.png](https://blog.kakaocdn.net/dn/8nQ5Q/btsvnf8lqkO/CjmjSeLLA21JMNkTrFTNWK/img.png)

**3. 세팅 여부 확인**

아래와 같이 JpashopApplication의 메소드 실행을 하게 되면,

![https://blog.kakaocdn.net/dn/bBG3b2/btsvnfN2xaC/hK6VtB6emATQjMtro7pwW0/img.png](https://blog.kakaocdn.net/dn/bBG3b2/btsvnfN2xaC/hK6VtB6emATQjMtro7pwW0/img.png)


실행 성공 시 Tomcat started on port(s): 8080 문구가 뜬다.

![https://blog.kakaocdn.net/dn/Eqn8Q/btsvkSTgYfa/8OulYNyM1IlR53iAkUtkYk/img.png](https://blog.kakaocdn.net/dn/Eqn8Q/btsvkSTgYfa/8OulYNyM1IlR53iAkUtkYk/img.png)


이후 웹페이지에서 아래 화면을 확인해주면 된다.

![https://blog.kakaocdn.net/dn/Cm3wQ/btsvMF5uKpw/5KWmt8pPY6kH3pikelZsZ1/img.png](https://blog.kakaocdn.net/dn/Cm3wQ/btsvMF5uKpw/5KWmt8pPY6kH3pikelZsZ1/img.png)


**+ 테스트 코드도 확인**

![https://blog.kakaocdn.net/dn/bvbHBu/btsvng0AGsK/3g3CorKqCAyYBP9VHEbuvk/img.png](https://blog.kakaocdn.net/dn/bvbHBu/btsvng0AGsK/3g3CorKqCAyYBP9VHEbuvk/img.png)


**4. 플러그인 설치**

![https://blog.kakaocdn.net/dn/nTrJR/btsvNyZlvL3/wSFELO8aMyke9gF2CZV73k/img.png](https://blog.kakaocdn.net/dn/nTrJR/btsvNyZlvL3/wSFELO8aMyke9gF2CZV73k/img.png)


![https://blog.kakaocdn.net/dn/cr2GHs/btsvMHPLPMe/ei3NfWhB7ShJtMIgJNmOk0/img.png](https://blog.kakaocdn.net/dn/cr2GHs/btsvMHPLPMe/ei3NfWhB7ShJtMIgJNmOk0/img.png)

롬복 사용시 Getter, Setter를 모두 만들어 준다.

## **라이브러리 살펴보기**

**1. cmd 창에서 확인 가능 (루트 폴더에서 열기)**

![https://blog.kakaocdn.net/dn/dR1eF5/btsvMLYXtPp/eWlyUiDkH8pfmt4CTebvr1/img.png](https://blog.kakaocdn.net/dn/dR1eF5/btsvMLYXtPp/eWlyUiDkH8pfmt4CTebvr1/img.png)


**2. IntelliJ에서 확인 가능**

![https://blog.kakaocdn.net/dn/cmSy7m/btsvDjaYqTI/a0kkhBH20rK8TfJ1Dae290/img.png](https://blog.kakaocdn.net/dn/cmSy7m/btsvDjaYqTI/a0kkhBH20rK8TfJ1Dae290/img.png)


## **View 환경설정**

Thymleaf 템플릿 엔진을 사용할 예정 ([Thymeleaf)](https://www.thymeleaf.org/)


웹 브라우저에서 열리는 natural templates 라는 장점이 있다. 스프링과 인티그레이션 되어 사용이 편리하다. 공식문서 메뉴얼을 봐야 이용법을 익힐 수 있다.

**1. HelloController 작성**

코드 경로 : jpashop/src/main/java/jpabook/jpashop/HelloController.java

```
package jpabook.jpashop;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {
    @GetMapping("hello")
    public String hello(Model model){//컨트롤러에서 뷰로 데이터를 넘길 수 있음
        model.addAttribute("data", "hello!!");//넘길 데이터return "hello";//화면 이름, .html 자동으로 붙음
    }
}
```

**2. Hello.html 작성**

코드 경로 : jpashop/src/main/resources/templates/hello.html

```
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Hello</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
<p th:text="'안녕하세요. ' + ${data}" >안녕하세요. 손님</p>
</body>
```

**3. 웹 브라우저에서 확인**

![https://blog.kakaocdn.net/dn/b3d5aQ/btsvMHI386z/sOwL1xmBOUUTUWBrtBntgk/img.png](https://blog.kakaocdn.net/dn/b3d5aQ/btsvMHI386z/sOwL1xmBOUUTUWBrtBntgk/img.png)


**4. index.html 작성**

순수한 html을 띄우고 싶을 때 사용하는 방법

코드 경로 : jpashop/src/main/resources/static/index.html

```
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Hello</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
Hello
<a href="/hello">hello</a>
</body>
</html>
```

**5. 웹 브라우저 상에서 확인**

![https://blog.kakaocdn.net/dn/bfvQxB/btsvlvKok2B/Kmd2AsMRomikvShUgKPjsk/img.png](https://blog.kakaocdn.net/dn/bfvQxB/btsvlvKok2B/Kmd2AsMRomikvShUgKPjsk/img.png)


- 정적인 컨텐츠는 static 패키지에, 템플릿 엔진으로 렌더링이 필요한 것들은 templates 패키지에 위치 시켜야 한다.

**+ 라이브러리 추가**

- 템플릿 엔진 수정 시, 서버 리스타트를 해주어야 반영이 된다.
- devtools 라이브러리 추가하면 편리하다.
- 캐시 제거와 리로딩이 가능하게 해준다.

```
implementation 'org.springframework.boot:spring-boot-devtools'
```

- 라이브러리 추가 후 서버를 띄웠을 때 로그가 아래와 같이 "restartedMain"으로 나온다면 세팅이 된 것이다.

![https://blog.kakaocdn.net/dn/mOFON/btsvM0V0l4x/uiaRTDM76s658FWSa3jT71/img.png](https://blog.kakaocdn.net/dn/mOFON/btsvM0V0l4x/uiaRTDM76s658FWSa3jT71/img.png)


- 이후 html 코드를 수정하고 IntelliJ > Build > Recompile "~.html" 을 해주면 수정 사항이 반영된다.

## **H2 데이터베이스 설치**

- H2 데이터베이스는 개발이나 테스트 용도로 가볍고 편리한 DB이며 웹 화면을 제공하는 특징이 있다.
- [H2 Database Engine](https://www.h2database.com/html/main.html)

- 설치 후 cmd를 관리자 권한으로 연 후 아래와 같은 명령어를 입력한다.

![https://blog.kakaocdn.net/dn/molkf/btsvQc2Dva5/tBJuZF6YPcRrt02PrnkTAk/img.png](https://blog.kakaocdn.net/dn/molkf/btsvQc2Dva5/tBJuZF6YPcRrt02PrnkTAk/img.png)


- 주소의 앞부분에 localhost를 덧붙여 준다. 혹은 우측 상단의 아이콘으로 접속도 가능하다.

![https://blog.kakaocdn.net/dn/kGsj4/btsvNr63FkV/k0h7LRsMlMkKpa0ldmkxGk/img.png](https://blog.kakaocdn.net/dn/kGsj4/btsvNr63FkV/k0h7LRsMlMkKpa0ldmkxGk/img.png)


![https://blog.kakaocdn.net/dn/bJjpoW/btsvNw1CvQJ/wLghQc1s972M1zaoOk2Aw1/img.png](https://blog.kakaocdn.net/dn/bJjpoW/btsvNw1CvQJ/wLghQc1s972M1zaoOk2Aw1/img.png)


- 아래와 같이 JDBC URL을 설정한다. (파일 모드 접)

![https://blog.kakaocdn.net/dn/xbOT7/btsvPqfAG8T/fcoWvE6n5rUTpUFbPt2lak/img.png](https://blog.kakaocdn.net/dn/xbOT7/btsvPqfAG8T/fcoWvE6n5rUTpUFbPt2lak/img.png)


- 이후에는 URL에 " jdbc:h2:tcp://localhost/~/jpashop " 를 입력하여 접근한다. (네트워크 모드로 tcpi를 통해서 접근)
- 웹 콘솔에서 이탈하면 db에서도 이탈하게 된다.

## **JPA와 DB 설정, 동작 확인**

**1. application.properties 삭제 후 application.yml 파일 생성 후 코드 작성**

- 자세한 내용은 스프링부트 메뉴얼에서 찾아야 한다. (https://docs.spring.io/spring-boot/docs/current/reference/html/)
- 운영환경에서는 로그들을 다 로거를 통해서 출력해야 한다.

```
spring:
  #h2 세팅
  datasource:
    url: jdbc:h2:tcp://localhost/~/jpashop'MVC=TRUE
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

logging:
  level:
    org.hibernate.SQL: debug #JPA나 Hibernate가 생성하는 SQL이 모두 보이는, logger를 통해
```

**2. Member 코드 작성**

코드 경로 : jpashop/src/main/java/jpabook/jpashop/Member.java

```
package jpabook.jpashop;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    private Long id;//식별자를 id로 하고 DB가 자동 생성하도록 함private String Username;

}
```

**3. MemberRepository 코드 작성**

코드 경로 : jpashop/src/main/java/jpabook/jpashop/MemberRepository.java

```
package jpabook.jpashop;

//Entity를 찾아주는, DAO와 유사import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository//컴포넌트 스캔의 대상, Spring Bean에 자동 등록public class MemberRepository {

    @PersistenceContext
    private EntityManager em;

//저장하는 코드public Long save(Member member){
        em.persist(member);
        return member.getId();
    }

    public Member find(Long id){
        return em.find(Member.class, id);
    }
}
```

**4. MemberRepositoryTest 코드 작성**

- Entity Manager를 통한 모든 데이터 변경은 항상 Transaction 안에서 이루어져야 한다.
- @Transactional은 Test 후 바로 Rollback한다. → @Rollback(false)
- findMember와 member 조회는 같다. 같은 Transaction 안에서 저장, 조회하면 영속성 Context가 같다.

```
package jpabook.jpashop;

//import jpabook.jpashop.domain.Member;//import jpabook.jpashop.repository.MemberRepository;import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
@RunWith(SpringRunner.class)
@SpringBootTest
public class MemberRepositoryTest {
    @Autowired MemberRepository memberRepository;
    @Test
    @Transactional
    @Rollback(false)
    public void testMember() {
//given
        Member member = new Member();
        member.setUsername("memberA");
//when
        Long savedId = memberRepository.save(member);
        Member findMember = memberRepository.find(savedId);
//then
        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
//Assertions.assertThat(findMember).isEqualTo(member); //JPA 엔티티 동일성 보장
    }
}
```

**5. H2 DB에서 확인**

![https://blog.kakaocdn.net/dn/A3XdZ/btsvPnK6mQc/O9tpTueXs2YqiK7iVhYHc0/img.png](https://blog.kakaocdn.net/dn/A3XdZ/btsvPnK6mQc/O9tpTueXs2YqiK7iVhYHc0/img.png)

![https://blog.kakaocdn.net/dn/bsALe5/btsvHgsykJ8/a1OPpVbbjb0Z3CK7LidsA0/img.png](https://blog.kakaocdn.net/dn/bsALe5/btsvHgsykJ8/a1OPpVbbjb0Z3CK7LidsA0/img.png)

MEMBER 확인 가능

**Test 코드 Run 시 계속 되는 Error..**

1. Setting > Gradle > Run Tests Using : IntelliJ

2. H2 삭제 후 재설치

여기까지 하였을 때 1번은 Test 코드 실행이 정상적이고 DB에 MEMBER와 memberA도 잘 올라 갔으나..

다시 Run 할 때나 cmd에서 돌릴 때는 Error가 발생한다.

## **쿼리 파라미터 로그 남기기**

- build.gradle에 아래와 같이 라이브러리 추가

```
	implementation 'com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.9.0'
```

- [gavlyukovskiy/spring-boot-data-source-decorator: Spring Boot integration with p6spy, datasource-proxy, flexy-pool and spring-cloud-sleuth (github.com)](https://github.com/gavlyukovskiy/spring-boot-data-source-decorator)
