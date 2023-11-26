# API 개발 고급
조회에 있어서 성능 최적화를 하자 
수정과 생성은 거의 문제가 발생하지 않는 편 

## 조회용 샘플 데이터 입력
* application.yml에서 ddl-auto를 create로 설정하면 만들어져 있던 table을 다 drop하고 다시 create한다 
* 에러 해결 
    * 조회용 샘플 데이터 입력에서 /orders로 주문내역 페이지를 들어갔을 때 

    *An error happened during template parsing (template: "class path resource [templates/order/orderList.html]")라는 500 에러가 뜨는데 
    * 이는 orderList.html에서 
        ```html
        <option th:each=
                    "status : ${T(com.jpabook.jpashop.domain.OrderStatus).values()}"
                    th:value="${status}"
                    th:text="${status}">option
        </option>
        ```

        com.jpabook.jpashop.domain.OrderStatus 로 자신이 설정한 패키지 구조에 맞게 수정하시면 됩니다! (기존에는 그냥 jpabook.jpashop.domain.OrderStatus라 되어있었음)

    * 참고: https://www.inflearn.com/questions/670263/%EC%A3%BC%EB%AC%B8%EB%82%B4%EC%97%AD-getmapping-parsing-error-%EA%B4%80%EB%A0%A8-%EC%A7%88%EB%AC%B8-%EB%93%9C%EB%A6%BD%EB%8B%88%EB%8B%A4
    