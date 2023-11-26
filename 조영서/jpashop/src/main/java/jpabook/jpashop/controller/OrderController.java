package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.service.ItemService;
import jpabook.jpashop.service.MemberService;
import jpabook.jpashop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController { //고객과 아이템 모두 선택해야 하기 때문에 dependency가 많이 필요하다
    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    @GetMapping(value = "/order")
    public String createForm(Model model) {
        List<Member> members = memberService.findMembers(); //멤버 가져오기
        List<Item> items = itemService.findItems(); //아이템 가져오기

        model.addAttribute("members", members); //모델에 넣기
        model.addAttribute("items", items);

        return "order/orderForm"; //이 HTML로 넘기기
    }

    @PostMapping(value = "order")
    public String order(@RequestParam("memberId") Long memberId,
                        @RequestParam("itemId") Long itemId,
                        @RequestParam("count") int count) { //변수에 바인딩하였다.
        orderService.order(memberId, itemId, count); //order 로직이 돌아간다.
        return "redirect:/orders"; //주문내용 목록으로 이동
    }

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
}
