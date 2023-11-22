# Spring JPA 스터디 6주차 과제

## 1. JPA 활용 2의 API 개발 중 회원 정보 삭제 API 만들기

jpashop/api/MemberApiController.java

jpashop/repository/MemberRepository.java

jpashop/service/MemberService.java 에 있습니다!!!

## 2. API 대해 알아보기

- **REST**
    - Representational State Transfer
    - 하나의 URI는 하나의 고유한 리소스(Resource)를 대표하도록 설계된다는 개념
- API 란
    - Application Programming Interface
    - 어떤 서버의 특정한 부분에 접속해서 그 안에 있는 데이터와 서비스를 이용할 수 있게 해주는 소프트웨어 도구
    - 운영체제와 응용프로그램 사이의 통신에 사용되는 언어나 메시지 형식
    - 객체를 반환하여 JSON 방식으로 바꾸어서 데이터만을 전달할 때 사용
- REST API
    - 컴퓨터와 컴퓨터, 서버와 클라이언트 등 다양한 애플레이케이션 연결 구조에서 프로그래밍 인터페이스 규격에 맞춰 자원의 이름으로 구분하여 자원의 상태를 주고받는 행위
    - **RESTFUL API**
        - REST의 원리를 따르는 시스템으로, REST를 사용했다 하여 모두가 RESTFUL 하지는 않음
        - REST API의 설계 규칙을 명확하게 지킨 시스템만이 RESTFUL 하다고 말할 수 있음
- 이전의 서버는 백엔드에서 데이터를 이용해서 **완성된 HTML**를 브라우저에게 전달해줘서, 브라우저는 단순한 뷰어 역할
- 요즘의 서버는 브라우저에서 필요한 **데이터**만을 전달하는 API 서버의 형태로 변화
    - 서버는 브라우저에게 **완성된 HTML**이 아닌, 브라우저에서 요구하는 **순수한 데이터 전달**
- ****@ResponseBody****
    - **데이터 자체를 전달**하기 위한 용도임을 알려주는 annotation
