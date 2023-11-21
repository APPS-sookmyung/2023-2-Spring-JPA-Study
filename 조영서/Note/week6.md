# JPA 활용 1 - Section 7

## **상품 주문**

- jpashop/src/main/java/jpabook/jpashop/controller/OrderController.java 생성
    - 상품 주문 기능의 Controller 작성

```
package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import jpabook.jpashop.service.MemberService;
import jpabook.jpashop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {//고객과 아이템 모두 선택해야 하기 때문에 dependency가 많이 필요하다private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    @GetMapping(value = "/order")
    public String createForm(Model model){
        List<Member> members = memberService.findMembers();//멤버 가져오기
        List<Item> items = itemService.findItems();//아이템 가져오기

        model.addAttribute("members", members);//모델에 넣기
        model.addAttribute("items", items);

        return "order/orderForm";//이 HTML로 넘기기
        }

    @PostMapping(value = "order")
    public String order(@RequestParam("memberId") Long memberId,
                        @RequestParam("itemId") Long itemId,
                        @RequestParam("count") int count){//변수에 바인딩하였다.
        orderService.order(memberId, itemId, count);//order 로직이 돌아간다.return "redirect:/orders";//주문내용 목록으로 이동
    }
}
```

- jpashop/src/main/resources/templates/order/orderForm.html 생성

```
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head th:replace="fragments/header :: header" />
<body>
<div class="container">
    <div th:replace="fragments/bodyHeader :: bodyHeader"/>
    <form role="form" action="/order" method="post">
        <div class="form-group">
            <label for="member">주문회원</label>
            <select name="memberId" id="member" class="form-control">
                <option value="">회원선택</option>
                <option th:each="member : ${members}"
                        th:value="${member.id}"
                        th:text="${member.name}" />
            </select>
        </div>
        <div class="form-group">
            <label for="item">상품명</label>
            <select name="itemId" id="item" class="form-control">
                <option value="">상품선택</option>
                <option th:each="item : ${items}"
                        th:value="${item.id}"
                        th:text="${item.name}" />
            </select>
        </div>
        <div class="form-group">
            <label for="count">주문수량</label>
            <input type="number" name="count" class="form-control" id="count"
                   placeholder="주문 수량을 입력하세요">
        </div>
        <button type="submit" class="btn btn-primary">Submit</button>
    </form>
    <br/>
    <div th:replace="fragments/footer :: footer" />
</div> <!-- /container -->
</body>
</html>
```

## **주문 목록 검색, 취소**

- OrderController.java 수정

```
    @GetMapping("/orders")
    public String orderlist(@ModelAttribute("orderSearch") OrderSearch orderSearch, Model model) {//상품 리스트를 검색하는 조건들이 다 담겨서 넘어온다.
        List<Order> orders = orderService.findOrders(orderSearch);
        model.addAttribute("orders", orders);

        return "order/orderList";
    }

    @PostMapping("/orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable("orderId") Long orderId){
        orderService.cancelOrder(orderId);
        return "redirect:/orders";
    }
```

- jpashop/src/main/resources/templates/order/orderList.html 생성

```
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head th:replace="fragments/header :: header"/>
<body>
<div class="container">
    <div th:replace="fragments/bodyHeader :: bodyHeader"/>
    <div>
        <div>
            <form th:object="${orderSearch}" class="form-inline">
                <div class="form-group mb-2">
                    <input type="text" th:field="*{memberName}" class="formcontrol" placeholder="회원명"/>
                </div>
                <div class="form-group mx-sm-1 mb-2">
                    <select th:field="*{orderStatus}" class="form-control">
                        <option value="">주문상태</option>
                        <option th:each=
                                        "status : ${T(jpabook.jpashop.domain.OrderStatus).values()}"
                                th:value="${status}"
                                th:text="${status}">option
                        </option>
                    </select>
                </div>
                <button type="submit" class="btn btn-primary mb-2">검색</button>
            </form>
        </div>
        <table class="table table-striped">
            <thead>
            <tr>
                <th>#</th>
                <th>회원명</th>
                <th>대표상품 이름</th>
                <th>대표상품 주문가격</th>
                <th>대표상품 주문수량</th>
                <th>상태</th>
                <th>일시</th>
                <th></th>
            </tr>
            </thead>
            <tbody>
            <tr th:each="item : ${orders}">
                <td th:text="${item.id}"></td>
                <td th:text="${item.member.name}"></td>
                <td th:text="${item.orderItems[0].item.name}"></td>
                <td th:text="${item.orderItems[0].orderPrice}"></td>
                <td th:text="${item.orderItems[0].count}"></td>
                <td th:text="${item.status}"></td>
                <td th:text="${item.orderDate}"></td>
                <td>
                    <a th:if="${item.status.name() == 'ORDER'}" href="#"
                       th:href="'javascript:cancel('+${item.id}+')'"
                       class="btn btn-danger">CANCEL</a>
                </td>
            </tr>
            </tbody>
        </table>
    </div>
    <div th:replace="fragments/footer :: footer"/>
</div> <!-- /container -->
</body>
<script>
    function cancel(id) {
    var form = document.createElement("form");
    form.setAttribute("method", "post");
    form.setAttribute("action", "/orders/" + id + "/cancel");
    document.body.appendChild(form);
    form.submit();
    }
</script>
</html>
```

# JPA 활용 2 - Section1

## **회원 등록 API**

- 단순히 SQL로 API를 끌어오지 않아도 된다.
- JPA는 Entity가 있기 때문에 API를 설계할 때 주의해야 한다.
- API를 test 하기 위해 Postman을 설치한다.
    - [Download Postman | Get Started for Free](https://www.postman.com/downloads/)


- 템플릿 엔진을 사용하여 렌더링 하는 Controller와 API 스타일의 Controller 패키지를 분리한다.
    - 예외 처리 등을 할 때 패키지 단위로 할 때가 많은데 화면과 API는 공통 처리 해야 하는 요소가 많이 다르다.
    - 화면은 템플릿 엔진에서 문제가 발생하면 공통 에러 HTML이 나와야 한다.
    - API는 공통 에러용 JSON API 스펙이 나가야 한다.
- @RestController = @Controller + @ResponseBody
- jpashop/src/main/java/jpabook/jpashop/api/MemberApiController.java 생성

```
package jpabook.jpashop.api;

import jakarta.validation.Valid;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){//json으로 온 body를 member에 그대로 매핑한다.
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @Data
    static class CreateMemberResponse{//응답값private Long id;

        public CreateMemberResponse(Long id){
            this.id = id;
        }
    }
}
```



Emtity에 다른 제약 조건을 걸지 않아 null 값으로 들어가기도 한다.

- 위 코드는 Entity와 API가 1:1로 매핑되어 있다.
    - Entity는 여러 곳에서 사용되어서 이리저리 바뀔 일이 많은데 Entity를 바꾸었다고 해서 API가 만들어둔 스펙 자체가 바꾸니다는 것이 문제점이다.
    - Entiy를 외부에서 json 오는 것을 바인딩 받는 데에 사용하면 안된다.
    - API 스펙을 위한 별도의 DTO를 만들어야 한다.
    - API 요청 스펙에 맞추어 별도의 DTO를 파라미터로 하여 받는 것이 좋다.
- 위에 맞게 코드를 추가해보자.

```
...

@RestController
@RequiredArgsConstructor
public class MemberApiController {

	...

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request){
        Member member = new Member();
        member.setName(request.getName());
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    ...

    @Data
    static class CreateMemberRequest{
        private String name;
    }

}
```



- 이렇게 수정하면 Member Entity가 변경되면 컴파일 오류가 나기 때문에 미리 수정이 가능하다.
    - 중간에서 파라미터와 Entity를 컨트롤하여 매핑해준다.
- 어떤 파라미터 값들이 넘어오는지도 DTO를 통해 명확히 알 수 있다.

## **회원 수정 API**

- MemberApiController.java 에 코드 추가

```
...

@RestController
@RequiredArgsConstructor
public class MemberApiController {

	...

//회원 수정 API@PutMapping("/api/v2/members/{id}")//id로 path variable을 가져간다public UpdateMemberResponse updateMemberV2(
            @PathVariable ("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request){

        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }
    @Data
    static class UpdateMemberRequest{
        private String name;
    }
    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse{
        private Long id;
        private String name;
    }
}
```


## **회원 조회 API**

- application.yml 에서 JPA의 설정을 아래와 같아 수정한다.

```
      ddl-auto: none #데이터를 한 번 넣어두면 계속 반복하여 DB의 데이터를 계속 쓸 수 있다.
```

- MemberApiController.java 에 코드 추가

```
//회원 조회 API@GetMapping("/api/v1/members")
    public List<Member> membersV1() {
        return memberService.findMembers();
    }
```

- 위 코드의 문제점
    - Entity를 직접 노출하므로 Entity의 정보들(orders)이 외부에 노출되어 버린다.
    - Entity에 Presentation 계층을 위한 Logic이 추가되었다.
    - Entity가 변경되면 API spec이 바뀐다.
    - 응답 spec을 맞추기 위한 로직이 추가된다.
    - 실무에서는 같은 Entity에 대해 API가 용도에 따라 다양하게 만들어지는데, 한 Entity에 각각의 API를 위한 Presentation 응답 Logic을 담기는 어렵다.
    - Array을 반환하면 spec을 확장할 수 없어 유연성이 떨어진다.
- 결국 API 응답 spec에 맞춘 DTO를 사용하는 것이 좋다.
- MemberApiController.java에 코드 추가

```
    @GetMapping("/api/v1/members")
    public Result membersV2() {
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream()//List Member을 List Member DTO로 변환
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());
        return new Result(collect);
    }

    @Data
    @AllArgsConstructor
    static class Result<T>{
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto{
        private String name;
    }
```

!https://blog.kakaocdn.net/dn/cdcSFs/btsAKbEE7NZ/f3cXUTdLPIkUSGiqCjgdKk/img.png

- 이렇게 하면 DTO와 API spec이 1:1 관계가 된다.
