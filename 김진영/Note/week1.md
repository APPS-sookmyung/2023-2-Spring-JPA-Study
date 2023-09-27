# 프로젝트 환경설정 

## 프로젝트 생성 / 라이브러리 살펴보기 
* 사용한 dependencies: lombok, web starter, jpa, thymeleaf, hibernate
* 오른쪽 사이드 바 Gradle 라는 메뉴의 Dependencies에서 각 라이브러리의 의존관계를 확인 가능 
    * slf4j: 단순한 인터페이스의 모음 라이브러리 (logback에 포함됨-의존관계) 
    * web starter에 spring core 포함되어있음 (의존관계)
    * 커넥션 풀: 부트 기본은 HikariCP
* 스프링 데이터 JPA는 스프링과 JPA를 먼저 이해하고 사용해야하는 응용기술임 

## View 환경 설정 
* Natural templates: 마크업을 깨지 않고 그대로 사용함 -> 웹 브라우저에서 열림 
    * 2점대 버전까지는 `<br>`을 그냥 쓰면 에러가 떴었음. 꼭 `<br></br>`로 닫아줘야했었는데 3점대부터는 해결됨 
* 스프링 부트 thymeleaf viewName 매핑: **`resources:templates/` +{ViewName}+ `.html`**
    * 그래서 return "hello"; 만 해도 hello.html로 연결되는 것 
    * 스프링 부트가 자동으로 매칭해줌 
* 정적(static) 컨텐츠는 `resources/static/` 안에 넣기 
    * ex. index.html 
* **spring-boot-devtools** 라이브러리는 개발을 편하게 해주는 도구 
    * 자동으로 reloading해줌 
    * 변경사항이 있는 해당 파일에 Build>Recompile "파일명" 을 하고 refresh하면 자동으로 반영됨 (서버를 껐다 켰다 할 필요가 없음)

## H2 데이터베이스 
* 개발/테스트 용도로 가볍고 편리한 db. 웹 콘솔 환경 제공 
* db 생성 후, `jdbc:h2:tcp://localhost/~/jpashop`로 h2 console 접속 
* **cmd로 h2.bat을 실행해놓아야 db가 꺼지지 않음!! 꼭 실행하고 진행하자** 

## 테스트코드 작성, 실행 동작 확인 
* 스프링부트 현재 버전에서는 JUnit5를 사용하므로 @Runwith을 @Extendwith으로 바꾸어 사용해야한다. 참고글: https://jordy-torvalds.tistory.com/102 
* 에러 해결 1: [Springboot] Execution failed for task ':test' 에러
    * https://mynameisleeminee.tistory.com/56
* 테스트 코드가 실행이 안될때 해결
    * yml파일에서 띄어쓰기가 제대로 되어있는지 확인할 것 