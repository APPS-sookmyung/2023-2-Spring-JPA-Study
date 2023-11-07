# 4주차 과제

- JPA는 다양한 쿼리 방법 지원
    - JPQL
    - JPA Criteria
    - QueryDSL
    - 네이티브 SQL
    - JDBC API 직접 사용. MyBatis, SpringJdbcTemplate 함께 사용

## JPQL

- Java Persistence Query Language
- 객체 지향 쿼리 언어
- 가장 단순한 조회 방법
    - EntityManager.find()
- JPA를 사용하면 엔티티 객체를 중심으로 개발
    - 문제는 검색쿼리!!
    - 검색을 할때도 케이블이 아닌 엔티티 객체를 대상으로 검색해야 함
    - 근데 모든 DB 데이터를 객체로 변환해서 검색하는 것은 불가능
    - 검색조건이 포함된 SQL 필요
    - 따라서 JPA는 SQL을 추상화한 JPQL이라는 객체 지향 쿼리 언어 제공
    - SQL과 문법이 유사하고, SELECT, FROM, WHERE, GROUP BY, HAVING, JOIN등을 지원
- 유의사항
    - from절에 들어가는 것은 객체다!
        - `select m from Member m where m.age > 8`
    - 엔티티와 속성은 대소문자를 구분
        - 예를 들면, Member 엔티티와 username 필드
    - JPQL 키워드는 대소문자 구분 안함
        - SELECT, FROM, where
    - 엔티티 이름 사용. 테이블 이름 아님
        - 엔티티명 Member
    - 별칭은 필수
        - Member의 별칭 m

```sql
String username = "java";
String jpql = "select m from Member m where m.username = :username";

List<Member> result = em.createQuery(query, Member.class).getResultList();
```

## queryDSL

- Spring Data JPA가 기본적으로 제공해주는 CRUD 메서드 및 쿼리 메서드 기능 사용→  원하는 조건의 데이터를 수집하기 위해서는 JPQL 작성 필요
- 복잡한 로직의 경우 개행이 포함된 쿼리 문자열이 상당히 길어짐.
- JPQL 문자열에 오타 혹은 문법적인 오류가 존재하는 경우, 정적 쿼리라면 어플리케이션 로딩 시점에 이를 발견할 수 있으나 그 외는 런타임 시점에서 에러 발생
- 이러한 문제를 어느 정도 해소하는데 기여하는 프레임워크가 **QueryDSL**
- 하이버네이트 쿼리 언어(HQL: Hibernate Query Language)의 쿼리를 타입에 안전하게 생성 및 관리해주는 프레임워크
- 장점
    1. 문자가 아닌 코드로 쿼리를 작성함으로써, 컴파일 시점에 문법 오류를 쉽게 확인할 수 있다.
    2. 자동 완성 등 IDE의 도움을 받을 수 있다.
    3. 동적인 쿼리 작성이 편리하다.
    4. 쿼리 작성 시 제약 조건 등을 메서드 추출을 통해 재사용할 수 있다.
    5. JPQL 문법과 유사한 형태로 작성할 수 있어 쉽게 적응할 수 있다.

```sql
String username = "java";

List<Member> result = queryFactory
        .select(member)
        .from(member)
        .where(usernameEq(username))
        .fetch();
```

- QueryDSL은 JPA 표준이 아니기 때문에 별도로 라이브러리를 추가해주어야 함
