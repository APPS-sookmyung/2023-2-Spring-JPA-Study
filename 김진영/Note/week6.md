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