# 6주차 과제

## 2. 회원정보 삭제 API 만들기 
* MemberApiController.java (70~80번째 줄)
    ```java
        @GetMapping("/api/v1/members/{id}")
        public DeleteMemberResponse deleteMemberV1(@PathVariable("id")Long id){
            memberService.delete(id);
            return new DeleteMemberResponse(id,"성공적으로 회원을 삭제했습니다");
        }
        @Data
        @AllArgsConstructor
        static class DeleteMemberResponse{
            private Long id;
            private String msg;
        }
    ```
* MemberService.java(48번째 줄~)
    ```java
        @Transactional
        public void delete(Long id){
            memberRepository.deleteById(id);
        }
    ```
* MemberRepository.java (44번째 줄~)
    ```java
    public void deleteById(Long id){
        Member member=findOne(id);
        em.remove(member);
    }
    ```
    * em.createQuery("delete from Member m where m.id=:id") ~..)이런식으로 작성하려고 했으나 `org.hibernate.query.illegal query operationexception` 에러가 발생 
    * 이유를 찾아보니 createQuery에서는 자동으로 삭제 조건에 부합하는 엔티티 목록을 select하기 때문
    * 따라서 em.remove()를 사용하였다 
    * 벌크 연산과 관련됨 
    * 참고글: https://velog.io/@park2348190/JPA%EC%9D%98-delete-%EC%BF%BC%EB%A6%AC-%EB%A9%94%EC%84%9C%EB%93%9C 

## 3. API에 대해서 조사하기 
* Application Programming Interface(애플리케이션 프로그램 인터페이스)
* 소프트웨어 응용 프로그램에서 다른 소프트웨어 구성 요소 또는 서비스와 상호 작용하기 위한 인터페이스를 제공하는 프로그래밍 기술
* API 종류 
    1. 웹 API :  인터넷을 통해 다른 웹 서비스나 애플리케이션과 통신하기 위해 설계된 API로, 일반적으로 HTTP를 사용하여 데이터를 전송
        * RESTFUL API, SOAP API 등 
    2. 라이브러리 API : 특정 프로그래밍 언어에서 사용되는 라이브러리나 프레임워크에서 제공되는 함수와 클래스 등을 사용하여 다른 소프트웨어 구성 요소와 상호 작용할 수 있도록 인터페이스를 제공
        * 다른 라이브러리나 프레임워크의 기능을 호출하거나 사용자 정의 함수와 클래스를 작성 가능 
* 참고글: https://velog.io/@kwontae1313/API%EB%9E%80-%EB%AC%B4%EC%97%87%EC%9D%BC%EA%B9%8C 
