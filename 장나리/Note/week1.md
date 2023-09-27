## 0.  강좌소개

- 스프링 부트 : 복잡하고 어려운 스프링 기술을 간결하고 쉽게 사용할 수 있도록 도와주는 기술
- JPA : 강력한 Java ORM 표준 기술
- 두 기술을 합치면 높은 개발 생산성을 유지하면서 빠르게 웹 어플리케이션 개발 가능
- 강의목표
    - 실무에서 웹 어플리케이션을 제대로 개발할 수 있도록!
    - 실무에 가까운 복잡한 예제 ..
1. SpringBoot, JPA, Hibernate, Gradle, Tomcat, Thymeleaf 세팅
2. 요구사항 분석
3. 도메인 모델 설계
4. 엔티티 설계 + 테이블 설계 → JPA로 엔티티와 테이블을 ORM으로 맵핑
5. 애플리케이션 아키텍쳐 구성
6. 핵심 비즈니스 로직 개발
7. 웹 계층 개발

## 1.  프로젝트 환경설정

### 1. 프로젝트 생성

- [Spring Boot Starter](https://start.spring.io/) - Spring Initializr
    - **Dependencies :** Spring Web, Thymeleaf, Spring Data JPA, H2 Database(개발, 테스트때 좋음), lombok(getter, setter 생성)
- 인텔리제이 lombok 플러그인 다운로드

### 2. 라이브러리 살펴보기

- 터미널에서 `./gradlew dependencies` : 의존관계 보여줌

스프링 부트 라이브러리

- spring-boot-starter-web
    - spring-boot-starter-tomcat: 톰캣 (웹서버)
    - spring-webmvc: 스프링 웹 MVC
    - spring-boot
        - spring-core
        - spring-context
- spring-boot-starter-thymeleaf: 타임리프 템플릿 엔진(View)
- spring-boot-starter-data-jpa
    - spring-boot-starter-aop
    - spring-boot-starter-jdbc
        - HikariCP 커넥션 풀 (부트 2.0 기본)
        - spring-jdbc
    - hibernate + JPA: 하이버네이트 + JPA
    - spring-data-jpa: 스프링 데이터 JPA
- spring-boot-starter(공통): 스프링 부트 + 스프링 코어 + 로깅
    - spring-boot
        - spring-core
    - spring-boot-starter-logging
        - logback, slf4j(로깅에 대한 인터페이스 모음)

test

- spring-starter-test
    - JUnit : 테스트 프레임워크
    - spring-test : 스프링 통합 테스트 지원
    - mockito-core : 목 라이브러리
    - assertj : 테스트 편하게 해주는 유틸리티

핵심 라이브러리

- 스프링 MVC
- 스프링 ORM
- JPA, 하이버네이트
- 스프링 데이터 JPA

기타 라이브러리

- H2 데이터베이스 클라이언트
- 커넥션 풀: 부트 기본은 HikariCP
- WEB(thymeleaf)
- 로깅 SLF4J & LogBack
- 테스트

**참고: 스프링 데이터 JPA는 스프링과 JPA를 먼저 이해하고 사용해야 하는 응용기술 !!!**

### 3. View 환경 설정

- thymeleaf 공식 사이트: https://www.thymeleaf.org/
- 스프링 공식 튜토리얼: https://spring.io/guides/gs/serving-web-content/
- 스프링부트 메뉴얼: https://docs.spring.io/spring-boot/docs/3.1.4/reference/html/

[Thymeleaf](https://www.thymeleaf.org/)

- 장점 : natural templates(html 마크업을 깨지않고 그대로 쓸 수 있음)
- 단점 : 메뉴얼을 봐야함..
- viewName 매핑
    - `resources:templates/ +{ViewName}+ .html`
    - jpabook.jpashop.HelloController
    ****
        
        ```jsx
        @Controller
          public class HelloController {
              @GetMapping("hello")
              public String hello(Model model) {
                  model.addAttribute("data", "hello!!");
                  return "hello";
              }
        }
        ```
        
    - thymeleaf 템플릿엔진 동작 확인(hello.html)
        
        ```jsx
        <!DOCTYPE HTML>
        <html xmlns:th="http://www.thymeleaf.org">
        <head>
                    <title>Hello</title>
                    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        </head>
        <body>
        <p th:text="'안녕하세요. ' + ${data}" >안녕하세요. 손님</p>
        </body>
        </html>
        ```
        

index.html 하나 만들기

- `static/index.html`
    
    ```jsx
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
    

**참고: spring-boot-devtools 라이브러리를 추가하면, html 파일을 컴파일만 해주면 서버 재시작 없이 View 파일 변경이 가능**

- 인텔리J 컴파일 방법: 메뉴 build Recompile
- `implementation 'org.springframework.boot:spring-boot-devtools'`

### 4. H2 데이터베이스 설치

- 개발이나 테스트 용도로 가볍고 편리한 DB, 웹 화면 제공
- **주의! Version 1.4.200 사용**
- h2 파일 → bin → sh h2.sh
- 데이터베이스 파일 생성 방법
    - jdbc:h2:~/jpashop (최소 한번)
    - ~/jpashop.mv.db 파일 생성 확인
    - 이후 부터는 jdbc:h2:tcp://localhost/~/jpashop 이렇게 접속
    - **1.4.200 버전에서는 MVCC 옵션을 사용하면 오류가 발생!!!!!!!**

### 5. JPA와 DB 설정, 동작확인

main/resources/application.yml

- `driver-class-name: org.h2.Driver`
    - 데이터베이스 커넥션과 관련된 데이터 소스 설정 완료
- `spring.jpa.hibernate.ddl-auto: create`
    - 이 옵션은 애플리케이션 실행 시점에 테이블을 drop 하고, 다시 생성한다.
- `logging.level.org.hibernate.SQL:debug`
    - Hibernate SQL 로그를 디버그 모드로 씀
    - JPA나 Hibernate가 생성하는 SQL이 다 보임
    - show_sql : 옵션은 System.out 에 하이버네이트 실행 SQL을 남김
- 띄어쓰기 주의!

회원 엔티티 만들어 동작 확인

- 회원 엔티티 생성
- 회원 레포지토리 생성
    - 엔티티 매니저 주입 - PersistenceContext
- 테스트 작성(command+shift+t)
    - @RunWith(SpringRunner.class)(JUnit4)->`@ExtendWith(SpringExtension.*class*)` (JUnit5)
    - 엔티티 매니저를 통한 모든 데이터 변경은 항상 트랜잭션 안에서! @Transactional(springframework)
    - yml 파일 -> `spring.jpa.hibernate.ddl-auto: create`        
    - transactional : 테스트가 끝나면 rollback → @Rollback(false)하면 데이터 볼 수 있음
        
- jar 빌드해서 동작 확인
- `implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.9.0")`
    - 파라미터 바인딩 출력
    - 개발에서만 사용하는 걸 권장
