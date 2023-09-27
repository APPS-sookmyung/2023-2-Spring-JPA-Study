# Week 1 과제: SpringBoot와 JPA에 대해 

## SpringBoot에 대해 
* 자바의 웹 프레임워크
* 기존 스프링 프레임워크(Spring framework)에 톰캣 서버를 내장하고 여러 편의 기능들을 추가함 

## JPA에 대해 
* 자바 진영에서 ORM(Object-Relational Mapping) 기술 표준으로 사용되는 인터페이스의 모음
    * ORM: 우리가 일반적으로 알고 있는 어플리케이션 Class와 RDB(Relational DataBase)의 테이블을 매핑(연결)한다는 뜻 
        * 어플리케이션의 객체를 RDB 테이블에 자동으로 영속화해주는 것 
* 실제적으로 구현된 것이 아니라 구현된 클래스와 매핑을 해주기 위해 사용되는 프레임워크 
* JPA를 구현한 대표적인 오픈 소스로는 Hibernate가 있음 
* 자바 어플리케이션에서 관계형 데이터베이스를 사용하는 방식을 정의한 인터페이스 
* JPA를 사용하여 SQL이 아닌 객체 중심으로 개발이 가능함 -> 생산성, 유지보수 GOOD 
* 자바에서는 부모-자식 클래스의 상속관계를 지원하나, 데이터베이스에는 이를 지원하지 않음 -> JPA가 이를 해결해줌 
* **스프링에서 흔히 사용하는 JPA는 JPA를 이용하는 spring-data-jpa 프레임워크지 JPA가 아님!** 
* 참고 링크(https://dbjh.tistory.com/77)