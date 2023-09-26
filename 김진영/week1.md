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