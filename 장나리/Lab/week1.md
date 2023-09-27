# Spring JPA 스터디 1주차 과제

## 1. SpringBoot

- 자바의 웹 프레임워크
- 기존 스프링 프레임워크(Spring Framework)에 톰캣 서버를 내장하고 여러 편의 기능들을 추가한 것
- 웹 프로그램을 쉽고 빠르게 만들어 주는 웹 프레임워크
- 웹 프레임워크
    - 웹 프로그램을 만들기 위한 스타터 키트
- 스프링 부트의 몇 가지 규칙만 익히면 누구나 빠르게 웹 프로그램 만들 수 있다!
    
    ```java
    @Controller
    public class HelloController {
        @GetMapping("/")
        @ResponseBody
        public String hello() {
            return "Hello World";
        }
    }
    ```
    
- 튼튼한 웹 프레임워크
    - 보안 공격을 기본으로 잘 막아줌
    - SQL 인젝션, XSS(cross-site scripting), CSRF(cross-site request forgery), 클릭재킹(clickjacking)과 같은 보안 공격을 기본으로 막아 줌
        - SQL 인젝션 : 악의적인 SQL을 주입하여 공격
        - XSS : 자바스크립트를 삽입해 공격
        - CSRF : 위조된 요청을 보내는 공격
        - 클릭재킹 : 사용자의 의도하지 않은 클릭을 유도하는 공격
- WAS가 따로 필요없음
    - 스프링만 사용하여 웹 애플리케이션을 개발한다면 웹 애플리케이션을 실행할 수 있는 톰캣과 같은 WAS(Web Application Server)가 필요
    - WAS의 종류 : Tomcat, Weblogic, WebSphere, JBoss, Jeus 등
    - 스프링부트에는 톰캣 서버가 내장되어 있고 설정도 자동 적용됨
    - 배포되는 jar 파일에도 톰캣서버가 내장되어 실행됨
- 설정이 쉬움

## 2. JPA(Java Persistence API)

- ORM(Object-Relational Mapping) : 애플리케이션 Class와 RDB(Relational DataBase)의 테이블을 매핑(연결)
- **JPA(Java Persistence API)**
    - **자바 진영에서 ORM(Object-Relational Mapping) 기술 표준으로 사용되는 인터페이스의 모음**
    - 실제적으로 구현된것이 아니라 구현된 클래스와 매핑을 해주기 위해 사용되는 프레임워크
    - JPA를 구현한 대표적인 오픈소스로는 Hibernate !
- 왜 JPA ?
    - 반복적인 CRUD SQL 처리 해줌
    - 매핑된 관계를 이용해서 SQL을 생성하고 실행 → 개발자는 어떤 SQL이 실행될지만 생각. 예측도 쉬움
    - 네이티브 SQL 이란 기능 제공 : 관계 매핑이 어렵거나 성능에 대한 이슈가 우려되는 경우 SQL을 직접 작성하여 사용 가능
    - 객체 중심으로 개발할 수 있다
        - 생산성 좋아지고 유지보수 수월
    - 패러다임의 불일치해결
        - JAVA는 부모클래스와 자식클래스의 관계 즉, 상속관계가 존재하는데 데이터베이스에서는 이러한 객체의 상속관계 지원 x
        - Album 클래스를 저장한다고 할때
            
            ```java
            // Album 객체저장
            jpa.persist(album);
            ```
            
        - JPA는 위의 코드를 아래의 쿼리로 변환해서 실행
            
            ```sql
            INSERT INTO ITEM (ID, NAME, PRICE) .....
            INSERT INTO ALBUM (ARTIST) .....
            ```
            
- **Hibernate**란?
    - **JPA를 구현한 구현체**
    - 대중적으로 많이 이용되는 JPA 구현체 중 하나
    - JPA의 핵심들인 EntityManagerFactory, EntityManager, EntityTransaction 등을 상속받아 구현
    - 다른 구현체들로는 EclipseLink나 DataNucleus 등이 있음
- **Spring Data JPA**란?
    - **JPA를 사용하기 편하도록 만들어놓은 모듈**
    - JPA를 한 단계 더 추상화시킨 Repository 인터페이스를 제공
    - Hibernate와 같은 JPA구현체를 사용해서 JPA를 사용
    - Spring Data JPA를 사용하면 사용자는 더욱 간단하게 데이터 접근이 가능해짐
