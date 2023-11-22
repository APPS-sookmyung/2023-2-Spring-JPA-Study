# 웹 계층 개발 

## 상품 주문 
* controller에서 직접 찾는 것보다 아래 코드를 권장하는 이유? 
    ```java
        @PostMapping("/order")
        public String order(@RequestParam("memberId") Long memberId,
                            @RequestParam("itemId") Long itemId,
                            @RequestParam("count") int count){
            orderService.order(memberId,itemId,count);
            return "redirect:/orders";
        }
    ```
    * controller 코드가 더러워질수도 
    * id만 넘겨서 깔끔하게 동작 
    * 서비스 계층에서는 엔티티에 의존 
        * 엔티티는 영속 상태로 흘러가기 때문에 더 깔끔하게 해결 가능 
    * 트랜잭션 안에서 엔티티를 조회해야 엔티티가 영속상태로 유지 가능 
    * 조회가 아닌 핵심 비즈니스 로직이 있는 경우 엔티티를 찾아서 넘기는 것보다 식별자(memberId, count 등)를 넘겨서 핵심 비즈니스 로직을 안에서 처리하자
        * 영속 컨텍스트가 존재하는 상태에서 조회 가능하기 때문 
        * 주문하면서 멤버나 아이템이 변하더라도 적용이 됨 (dirty checking)
        * 만약 트랜잭션 없이 밖에서 조회하게 되면 영속성 컨텍스트 안에서 조회하는 것이 아니기 떄문에 애매해짐 

## 주문 목록 검색, 취소 
* 코드 
    ```java
        @GetMapping("/orders")
        public String orderList(@ModelAttribute("orderSearch")OrderSearch orderSearch, Model model){
            List<Order> orders=orderService.findOrders(orderSearch);
            model.addAttribute("orders",orders);

            return "order/orderList";
        }
        
        @PostMapping("/orders/{orderId}/cancel")
        public String cancelOrder(@PathVariable("orderId")Long orderId){
            orderService.cancelOrder(orderId);
            return "redirect:/orders"; 
        }
    ```
    * @ModelAttribute를 통해 "orderSearch" 값을 가져옴 
    * @PathVariable를 통해 바인딩 

# 2편 - API 개발과 성능 최적화

## 회원 등록 API 
* @RestController는 @Controller + @ResponseBody 
    * REST API 스타일로 만들어준다고 지정 
    * ResponseBody는 데이터를 json 방식으로 바로 보낸다는 의미 
* @RequestBody는 json으로 온 body를 Member로 바꾸어줌 (MVC)
* @Valid를 통해 @NotEmpty 등이 유효한지 검사 
    ```java
        @PostMapping("/api/v1/members")
        public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){
            Long id= memberService.join(member);
            return new CreateMemberResponse(id);
        }
    ```
* 그러나 심각한 문제를 일으킴 -> presentation 매칭을 위한 검증 로직이 다 엔티티에 들어가있음 (NotEmpty 같은거)
    * 어떤 api에서는 @NotEmpty가 필요없을 수도 있기 때문 
    * name을 username으로 바꿔버리면 api가 깨져버림 
    * 엔티티는 여러곳에서 사용하기 때문에 api 스펙을 위해 별도의 dto를 만들어야함 
* 그래서 이런식으로 만들자
    ```java
        @PostMapping("/api/v2/members")
        public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request){
            Member member=new Member();
            member.setName(request.getName());

            Long id=memberService.join(member);
            return new CreateMemberResponse(id);
        }

        @Data
        static class CreateMemberRequest{
            @NotEmpty
            private String name;
        }
    ```
    * 엔티티에서 username으로 바꾸더라도 api에 전혀 영향을 주지 않음 

## 회원 수정 API
* update에서 Member 엔티티를 넘겨주는 방법도 있으나 그러면 쿼리와 커맨드를 같이 날리게 되어버리므로 따로 쿼리문을 작성하여 리턴해주도록 함 
    ```java
        @PutMapping("/api/v2/members/{id}")
        public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id,
                                                @RequestBody @Valid UpdateMemberRequest request){
            memberService.update(id,request.getName());
            Member findMember=memberService.findOne(id);
            return new UpdateMemberResponse(findMember.getId(), findMember.getName());
        }
    ```

## 회원 조회 API
* application.yml에서 jpa.hibernate.ddl-auto 를 none으로 바꿈 -> 넣어둔 데이터들을 계속 쓸 수 있게됨 
* 아래 코드에는 문제가 발생
    ```java
        @GetMapping("/api/v1/members")
        public List<Member> membersV1(){
            return memberService.findMembers();
        }
    ```
    * 엔티티의 orders까지 가져와버림 (순수하게 회원 정보만 불러오고 싶을 경우)
        * @JsonIgnore 을 붙이면 됨 
        * 그치만 다른 api를 만들때 문제가 발생함 
        * 엔티티에 프레젠테이션 로직이 추가되어버리면 안됨 
        * 엔티티 의존관계가 깨져버리고 수정이 어려워짐 
        * api 스펙이 변경되어버리는 문제가 발생할 수도 
    * 따라서 api 응답 스펙에 맞추어 별도의 dto를 반환해야함 
* 따래서 아래와 같이 작성하자
    ```java
            @GetMapping("/api/v2/members")
            public Result membersV2(){
                List<Member> findMembers= memberService.findMembers();
                List<MemberDto> collect= findMembers.stream()
                        .map(m-> new MemberDto(m.getName()))
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
* 엔티티를 절대 외부에 반환하지 말자 