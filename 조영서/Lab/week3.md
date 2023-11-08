# JPQL & QueryDSL

## **[Intro](https://y-seo.tistory.com/entry/JPQL%EC%97%90-%EB%8C%80%ED%95%B4%EC%84%9C#Intro)**

- JPA는 객체 중심적으로 코드를 작성할 수 있게 해주는데 그렇게 되면 JPA만으로는 모든 쿼리를 커버할 수 없다.
- 따라서 JPA는 객체를 검색(조회,select)할 수 있는 다양한 query 방법을 지원한다.
    - JPQL
    - JPA Criteria
    - QueryDSL
    - Native SQL
    - 등등..
- 이 포스팅에서는 주로 사용되는 JPQL과 QueryDSL에 대해 다루고자 한다.

## **[JPQL의 개념](https://y-seo.tistory.com/entry/JPQL%EC%97%90-%EB%8C%80%ED%95%B4%EC%84%9C#JPQL%EC%-D%--%--%EA%B-%-C%EB%--%--)**

- Java Persistence Query Language
- JPQL은 엔티티 객체를 조회하는 객체지향 쿼리이다.
- JPQL은 가장 중요한 객체지향 쿼리 언어이다.
- JPA는 JPQL을 기반으로 하는 다양한 쿼리 서비스를 지원한다.
    - JPA Criteria, QueryDSL, Natice SQL 등..

## **[JPQL의 특징](https://y-seo.tistory.com/entry/JPQL%EC%97%90-%EB%8C%80%ED%95%B4%EC%84%9C#JPQL%EC%-D%--%--%ED%-A%B-%EC%A-%--)**

- SQL과 문법이 비슷하다.
    - 오히려 SQL보다 더 간결하다.
- ANSI 표준에서 지원하는 쿼리 명령문을 모두 제공한다.
    - SELECT, FROM, WHERE 등
- 특정 DB의 SQL에 의존하지 않도록 SQL을 추상화 하여 제공한다.
    - DB를 변경하고 싶으면 DB Dialect만 변경하면 된다.
- 테이블이 아닌 객체를 대상으로 검색하는 쿼리이다.
- 결국에는 SQL로 변환된다.

## **[JPQL의 단점](https://y-seo.tistory.com/entry/JPQL%EC%97%90-%EB%8C%80%ED%95%B4%EC%84%9C#JPQL%EC%-D%--%--%EB%-B%A-%EC%A-%--)**

- 문자열로 작성되기 때문에 컴파일 시 에러를 잡기 어렵다.
    - 추후 배포 시에 문제가 생길 수 있다.
- 동적으로 쿼리 언어를 작성하는 데에 효율적이지 못하다.
    - 특정 조건에 따라 참일 때 A 쿼리를 실행하고, 거짓일 때는 B 쿼리를 실행하는 행위

## **[JPQL 문법](https://y-seo.tistory.com/entry/JPQL%EC%97%90-%EB%8C%80%ED%95%B4%EC%84%9C#JPQL%--%EB%AC%B-%EB%B-%--)**

- 기본 규칙
    - 대소문자 구분을 한다.
        - SELECT, AS와 같은 키워드는 구분하지 않아도 된다.
    - 엔티티 이름 사용한다.
        - @Entity(name="~~") 로 설정할 수 있다.
        - 테이블 이름을 사용하지 않는다.
    - 별칭을 필수적으로 명시/사용한다.
        - AS 키워드를 생략할 수 있다.
- 기본 문법

```java
SELECT m FROM Member AS m WHERE m.username = 'xxx'
```

## **[QueryDSL의 개념](https://y-seo.tistory.com/entry/JPQL%EC%97%90-%EB%8C%80%ED%95%B4%EC%84%9C#QueryDSL%EC%-D%--%--%EA%B-%-C%EB%--%--)**

- JPQL의 단점을 보완하기 위해 사용하는 프레임워크다.
- 정적 타입을 이용해 쿼리를 생성해준다.

## **[QueryDSL의 장점](https://y-seo.tistory.com/entry/JPQL%EC%97%90-%EB%8C%80%ED%95%B4%EC%84%9C#QueryDSL%EC%-D%--%--%EC%-E%A-%EC%A-%--)**

- 코드로 쿼리를 작성하므로 컴파일 시점에서 오타를 찾아낼 수 있다.
- 자동 완성으로 코드 작성이 간편하다.
- 동적 쿼리 작성이 편리하다.
- 코드를 재사용할 수 있다.
- JPQL과 유사한 문법이라 쉽게 적응할 수 있다.
