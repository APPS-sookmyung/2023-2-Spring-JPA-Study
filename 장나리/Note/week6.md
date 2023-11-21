# 섹션 7 웹 계층 개발

## 8.  상품 주문

- OrderController 생성
    - 고객이랑 아이템 다 선택할 수 있어야 함
    - findItems()로 아이템을 가져온 후 모델에 담아서 orderForm으로 넘김
    - select box - `th:each` 로 담을 수 있음
    - `RequestParam` : Http 요청 파라미터 값
    - 지금은 상품 하나만 가능
    
    ```java
    @Controller
    @RequiredArgsConstructor
    public class OrderController {
    
        private final OrderService orderService;
        private final MemberService memberService;
        private final ItemService itemService;
    
        @GetMapping("/order")
        public String createForm(Model model){
            List<Member> members = memberService.findMembers();
            List<Item> items = itemService.findItems();
    
            model.addAttribute("members",members);
            model.addAttribute("items",items);
    
            return "order/orderForm";
        }
    
        @PostMapping("/order")
        public String order(@RequestParam("memberId") Long memberId,
                            @RequestParam("itemId") Long itemId,
                            @RequestParam("count") int count
                            ){
            orderService.order(memberId,itemId,count);
            return "redirect:/orders";
        }
    }
    ```
    
    - `orderService.order(memberId,itemId,count);`
    controller에서 멤버랑 아이템 찾아서 넘겨도 됨
        - 이렇게 하면 controller에서 찾으면 controller로직이 더러워지기도 함
        - 단순화할 수 있음
        - id만 넘겨도 잘 동작하니까 깔끔
        - 바깥에서는 엔티티를 몰라도 됨
        - 할 수 있는게 더 많아짐
        - 엔티티가 영속상태로 흘러가 깔끔하게 문제 해결 가능
    - 조회가 아닌 핵심 비즈니스 로직이 있는 경우 식별자만 넘겨주고 안에서 찾아서 넘기도록

## 9. 주문 목록 검색, 취소

주문 목록 검색

- 리포지토리에 접근하는 것은 단순한 조회의 경우에는 그냥 호출해도 됨
- 서비스에 한번에 위임하도록 함

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/ffd544eb-a3b5-4d33-ada6-befe785ca880/4803df8e-511a-4756-b4b0-825ce37a25c1/Untitled.png)

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/ffd544eb-a3b5-4d33-ada6-befe785ca880/e23c807f-e7d3-4a15-8cb6-10cdabe2a15f/Untitled.png)

```java
@GetMapping("/orders")
    public String orderList(@ModelAttribute("orderSearch")OrderSearch orderSearch, Model model){
        List<Order> orders = orderService.findOrders(orderSearch);
        model.addAttribute("orders",orders);
//        model.addAttribute("orderSearch",orderSearch);
        return "order/orderList";
    }
```

- @ModelAttribute("orderSearch")
    - 모델박스에 자동으로 담김
    - `model.addAttribute("orderSearch",orderSearch);` 생략된거라고 봐도 됨.
- 루프 돌려서 뿌림
    
    ```java
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
    ```
    

취소

```java
<td>
  <a th:if="${item.status.name() == 'ORDER'}" href="#"
     th:href="'javascript:cancel('+${item.id}+')'"
     class="btn btn-danger">CANCEL</a>
</td>
```

- 상태가 order면 취소버튼 활성화 → js 호출

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/ffd544eb-a3b5-4d33-ada6-befe785ca880/02840ad3-a210-43d7-8d4f-29014e38be7c/Untitled.png)

# **실전! 스프링 부트와 JPA 활용2 - API 개발과 성능 최적화**

# 섹션 0 강좌소개

- REST API 개발
- 성능 최적화

# 섹션 1 API 개발 기본

## 1. 회원 등록 API

- Api 매우 중요!!
- 단순하게 SQL 써서 api를 끌어오는게 아니라, JPA에서 엔티티가 있는 상태에서 API를 만드는 것은 완전 다른 차원
- 템플릿 엔진을 사용하는 컨트롤러와 api 스타일의 컨트롤러 패키지를 분리하는게 좋음
- 예외처리를 한다거나 할때 패키지 단위로 공통 처리
- `@Controller`+`@ResponseBody` = `@RestController`
- `@ResponseBody` : 데이터 자체를 바로 json이나 xml로 보낼때 사용

```java
@RestController
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @Data
    static class CreateMemberResponse{
        private Long id;
        public CreateMemberResponse(Long id){
            this.id = id;
        }
    }
}
```

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/ffd544eb-a3b5-4d33-ada6-befe785ca880/71f76d1f-1fc2-4050-aba7-49a64711645a/Untitled.png)

- 이름 없이 보내면 → 그냥 null 로 들어감

```java
public class Member {
...
    @NotEmpty
    private String name;
...
}
```

- 문제점
    - presentation 계층을 위한 검증로직이 entity에 들어가 있음
        - 어떤 api에선 notempty가 필요 없을 수도
    - 엔티티 명 수정하면 api 스펙 변함
- 엔티티가 변했다고 해서 api 스펙 변하는 건 안됨
- api 스펙을 위해 별도의 DTO(Data Transfer Object) 만들어주기!
- 등록도 여러개가 있을 수 있음 → 조금만 복잡해져도 엔티티를 외부에 노출 x
- api를 만들때 항상 엔티티를 파라미터로 받지 x

```java
@PostMapping("/api/v2/members")
public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request){
    Member member = new Member();
    member.setName(request.getName());

    Long id = memberService.join(member);
    return new CreateMemberResponse(id);
}
@Data
static class CreateMemberRequest{
    private String name;
}
```

- 별도의 dto 사용
    - 파라미터와 엔티티 맵핑
    - api 스펙 변하지 않음
    - 엔티티는 파라미터가 뭐가 올지 모름.
    - dto는 뭐가 넘어오는지 한번에 알 수 있음
    - 유지보수 할 때 좋음

## 2. 회원 수정 API

수정 - PUT

- PUT: 똑같은 수정을 여러번해도 결과 똑같음
- update request dto, update response dto 따로 만듦
    - 등록이랑 수정은 api 스펙이 다른 경우 많음
    - 수정은 제한적임
- 수정 - 변경감지 !!!

```java
//MemberService.java
@Transactional
public void update(Long id, String name) {
    Member member = memberRepository.findOne(id);
    member.setName(name);
}
```

- 커맨드와 쿼리 분리하자!

```java
//MemberApiController.java
@PutMapping("api/v2/members/{id}")
public UpdateMemberResponse updateMemberV2(
        @PathVariable("id") Long id,
        @RequestBody @Valid UpdateMemberRequest request){
    memberService.update(id, request.getName());
    Member findMember = memberService.findOne(id);
    return new UpdateMemberResponse(findMember.getId(), findMember.getName());
}
@Data
@AllArgsConstructor
static class UpdateMemberResponse{
    private Long id;
    private String name;
}
@Data
static class UpdateMemberRequest{
    private String name;
}
```

## 3. 회원 조회 API

- 단순 조회니까 데이터나 테이블 변경할 일 없음
- `ddl-auto: none` : 테이블 drop 하지 않음. 한번 데이터 넣으면 계속 쓸 수 있음
- V1 : 요청 값으로 Member 엔티티를 직접 받는다.
    
    ```java
    @GetMapping("/api/v1/members")
        public List<Member> memberV1(){
            return memberService.findMembers();
        }
    ```
    
- 지금은 결과가
    
    ```json
    [
        {
            "id": 1,
            "name": "hello",
            "address": null,
            "orders": []
        },
        {
            "id": 2,
            "name": "hello3",
            "address": null,
            "orders": []
        }
    ]
    ```
    
- 우린 order 정보가 아닌 회원 정보가 필요함 - 엔티티를 직접 노출 시키면 안됨
- @JsonIgnore : 원하는 걸 뺄 수 있지만 엔티티가 영향을 받게 되므로 이렇게 하면 안됨!!
- 문제점
    - 엔티티에 프레젠테이션 계층을 위한 로직이 추가된다.
    - 엔티티에 API 검증을 위한 로직이 들어간다. (@NotEmpty 등등)
    - 실무에서는 회원 엔티티를 위한 API가 다양하게 만들어지는데, 한 엔티티에 각각의 API를 위한 모든 요청 요구사항을 담기는 어렵다.
    - 엔티티가 변경되면 API 스펙이 변한다.
- 결론
    - API 요청 스펙에 맞추어 별도의 DTO를 파라미터로 받는다.
- 번외
    - array로 넘어오는데, 똑같은 object가 들어있음
    - 근데 다른 값이 추가되면 api 스펙이 깨짐
    - 이런식으로 해야함
        
        ```json
        {
            "count" : 4
            "data" : [
                                {
                                    "id": 1,
                                    "name": "hello",
                                    "address": null,
                                },
                                {
                                    "id": 2,
                                    "name": "hello3",
                                    "address": null,
                                }
                            ]
        }
        ```
        
- V2: 요청 값으로 Member 엔티티 대신에 별도의 DTO를 받는다.
    
    ```java
    @GetMapping("/api/v2/members")
    public Result memberV2(){
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream()
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
    static class MemberDto {
        private String name;
    }
    ```
    
    ```json
    //결과
    {
        "count": 4,
        "data": [
            {
                "name": "hello"
            },
            {
                "name": "hello3"
            },
            {
                "name": "장나리"
            },
            {
                "name": "member"
            }
        ]
    }
    ```
